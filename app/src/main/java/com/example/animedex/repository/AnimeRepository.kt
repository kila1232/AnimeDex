package com.example.animedex.repository

import androidx.core.text.HtmlCompat
import com.example.animedex.model.AniListGraphQlRequest
import com.example.animedex.model.AniListMedia
import com.example.animedex.model.AnimeDto
import com.example.animedex.model.AnimeImageFormat
import com.example.animedex.model.AnimeImages
import com.example.animedex.model.NamedResourceDto
import com.example.animedex.network.AniListApiService
import com.example.animedex.network.RetrofitClient
import com.example.animedex.network.TranslationApiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class AnimeRepository(
    private val apiService: AniListApiService = RetrofitClient.apiService,
    private val translationService: TranslationApiService = RetrofitClient.translationService
) {
    // Repository menjadi satu-satunya tempat Composable/ViewModel meminta data dari network.
    suspend fun getAnimeList(query: String? = null): List<AnimeDto> = withContext(Dispatchers.IO) {
        val normalizedQuery = query.normalizedQuery()
        try {
            retryNetworkRequest {
                val request = AniListGraphQlRequest(
                    query = ANIME_LIST_QUERY,
                    variables = mapOf(
                        "search" to normalizedQuery,
                        "page" to 1,
                        "perPage" to 20
                    )
                )
                apiService.getAnimeList(request)
                    .data
                    ?.page
                    ?.media
                    .orEmpty()
                    .map { it.toAnimeDto() }
            }
        } catch (exception: HttpException) {
            if (exception.isBusyServer()) {
                FallbackAnimeData.list(normalizedQuery)
            } else {
                throw exception
            }
        }
    }

    suspend fun getAnimeDetail(animeId: Int): AnimeDto = withContext(Dispatchers.IO) {
        try {
            retryNetworkRequest {
                val request = AniListGraphQlRequest(
                    query = ANIME_DETAIL_QUERY,
                    variables = mapOf("id" to animeId)
                )
                apiService.getAnimeDetail(request)
                    .data
                    ?.media
                    ?.toAnimeDto()
                    ?.withIndonesianSynopsis()
                    ?: error("Detail anime tidak ditemukan di AniList.")
            }
        } catch (exception: HttpException) {
            if (exception.isBusyServer()) {
                FallbackAnimeData.detail(animeId) ?: throw exception
            } else {
                throw exception
            }
        }
    }

    // Retry otomatis membantu saat AniList sedang rate limit atau gateway timeout sementara.
    private suspend fun <T> retryNetworkRequest(block: suspend () -> T): T {
        repeat(MAX_RETRY_COUNT) { attempt ->
            try {
                return block()
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (!exception.canRetry() || attempt == LAST_RETRY_INDEX) {
                    throw exception
                }
                delay(RETRY_DELAY_MS * (attempt + 1))
            }
        }
        error("Retry request gagal dijalankan.")
    }

    private fun Exception.canRetry(): Boolean = when (this) {
        is HttpException -> code() in RETRYABLE_HTTP_CODES
        else -> true
    }

    private fun HttpException.isBusyServer(): Boolean = code() in BUSY_SERVER_HTTP_CODES

    private fun AniListMedia.toAnimeDto(): AnimeDto {
        val imageUrl = coverImage?.large ?: coverImage?.medium
        val largeImageUrl = coverImage?.extraLarge ?: coverImage?.large ?: coverImage?.medium

        return AnimeDto(
            id = id,
            title = title?.romaji ?: title?.english ?: title?.native,
            titleEnglish = title?.english,
            synopsis = description?.toPlainText(),
            episodes = episodes,
            status = status.toDisplayStatus(),
            score = averageScore?.div(10.0),
            rating = if (isAdult == true) "Dewasa" else "Umum",
            year = seasonYear,
            images = AnimeImages(
                jpg = AnimeImageFormat(
                    imageUrl = imageUrl,
                    largeImageUrl = largeImageUrl
                )
            ),
            genres = genres.orEmpty().mapIndexed { index, genre ->
                NamedResourceDto(id = index + 1, name = genre)
            },
            studios = studios?.nodes.orEmpty()
        )
    }

    private suspend fun AnimeDto.withIndonesianSynopsis(): AnimeDto {
        val originalSynopsis = synopsis?.takeIf { it.isNotBlank() } ?: return this
        return copy(synopsis = translateSynopsis(originalSynopsis))
    }

    private suspend fun translateSynopsis(text: String): String {
        return try {
            val translatedChunks = text.chunkForTranslation().map { chunk ->
                translationService.translateToIndonesian(chunk)
                    .responseData
                    ?.translatedText
                    ?.toPlainText()
                    ?.takeIf { it.isNotBlank() }
                    ?: chunk
            }

            translatedChunks.joinToString(" ").ifBlank { text }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            text
        }
    }

    private fun String.chunkForTranslation(maxLength: Int = 450): List<String> {
        val sentences = replace("\n", " ")
            .split(Regex("(?<=[.!?])\\s+"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        val chunks = mutableListOf<String>()
        val currentChunk = StringBuilder()

        sentences.forEach { sentence ->
            if (sentence.length > maxLength) {
                if (currentChunk.isNotBlank()) {
                    chunks.add(currentChunk.toString())
                    currentChunk.clear()
                }
                chunks.addAll(sentence.chunked(maxLength))
            } else if (currentChunk.length + sentence.length + 1 > maxLength) {
                chunks.add(currentChunk.toString())
                currentChunk.clear()
                currentChunk.append(sentence)
            } else {
                if (currentChunk.isNotBlank()) currentChunk.append(' ')
                currentChunk.append(sentence)
            }
        }

        if (currentChunk.isNotBlank()) chunks.add(currentChunk.toString())
        return chunks.ifEmpty { listOf(this) }
    }

    private fun String.toPlainText(): String =
        HtmlCompat.fromHtml(this, HtmlCompat.FROM_HTML_MODE_LEGACY)
            .toString()
            .replace(Regex("\\n{3,}"), "\n\n")
            .trim()

    private fun String?.toDisplayStatus(): String = when (this) {
        "FINISHED" -> "Finished"
        "RELEASING" -> "Currently Airing"
        "NOT_YET_RELEASED" -> "Not Yet Released"
        "CANCELLED" -> "Cancelled"
        "HIATUS" -> "Hiatus"
        else -> this ?: "-"
    }

    private fun String?.normalizedQuery(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

    private companion object {
        const val MAX_RETRY_COUNT = 3
        const val LAST_RETRY_INDEX = MAX_RETRY_COUNT - 1
        const val RETRY_DELAY_MS = 900L
        val RETRYABLE_HTTP_CODES = setOf(429, 500, 502, 503, 504)
        val BUSY_SERVER_HTTP_CODES = setOf(429, 502, 503, 504)
        val ANIME_FIELDS = """
            id
            title {
                romaji
                english
                native
            }
            description(asHtml: false)
            episodes
            status
            averageScore
            isAdult
            seasonYear
            coverImage {
                extraLarge
                large
                medium
            }
            genres
            studios(isMain: true) {
                nodes {
                    id
                    name
                }
            }
        """
        val ANIME_LIST_QUERY = """
            query AnimeList(${"$"}search: String, ${"$"}page: Int, ${"$"}perPage: Int) {
                Page(page: ${"$"}page, perPage: ${"$"}perPage) {
                    media(type: ANIME, search: ${"$"}search, sort: SCORE_DESC, isAdult: false) {
                        $ANIME_FIELDS
                    }
                }
            }
        """
        val ANIME_DETAIL_QUERY = """
            query AnimeDetail(${"$"}id: Int) {
                Media(id: ${"$"}id, type: ANIME) {
                    $ANIME_FIELDS
                }
            }
        """
    }
}
