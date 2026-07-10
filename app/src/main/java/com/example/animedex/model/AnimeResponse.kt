package com.example.animedex.model

import com.google.gson.annotations.SerializedName

data class AnimeDto(
    @SerializedName(value = "mal_id", alternate = ["id"])
    val id: Int = 0,
    val title: String? = null,
    @SerializedName("title_english")
    val titleEnglish: String? = null,
    val synopsis: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val score: Double? = null,
    val rating: String? = null,
    val year: Int? = null,
    val images: AnimeImages? = null,
    val genres: List<NamedResourceDto>? = emptyList(),
    val studios: List<NamedResourceDto>? = emptyList()
) {
    val displayTitle: String
        get() = title.orEmpty().ifBlank { titleEnglish.orEmpty().ifBlank { "Tanpa judul" } }

    val posterUrl: String?
        get() = images?.jpg?.largeImageUrl
            ?: images?.jpg?.imageUrl
            ?: images?.webp?.largeImageUrl
            ?: images?.webp?.imageUrl

    val genreText: String
        get() = genres.orEmpty().joinToString(", ") { it.name }.ifBlank { "-" }

    val studioText: String
        get() = studios.orEmpty().joinToString(", ") { it.name }.ifBlank { "-" }
}

data class AnimeImages(
    val jpg: AnimeImageFormat? = null,
    val webp: AnimeImageFormat? = null
)

data class AnimeImageFormat(
    @SerializedName("image_url")
    val imageUrl: String? = null,
    @SerializedName("large_image_url")
    val largeImageUrl: String? = null
)

data class NamedResourceDto(
    @SerializedName(value = "mal_id", alternate = ["id"])
    val id: Int = 0,
    val name: String = ""
)

data class AniListGraphQlRequest(
    val query: String,
    val variables: Map<String, Any?> = emptyMap()
)

data class AniListListResponse(
    val data: AniListListData? = null
)

data class AniListListData(
    @SerializedName("Page")
    val page: AniListPage? = null
)

data class AniListPage(
    val media: List<AniListMedia> = emptyList()
)

data class AniListDetailResponse(
    val data: AniListDetailData? = null
)

data class AniListDetailData(
    @SerializedName("Media")
    val media: AniListMedia? = null
)

data class AniListMedia(
    val id: Int = 0,
    val title: AniListTitle? = null,
    val description: String? = null,
    val episodes: Int? = null,
    val status: String? = null,
    val averageScore: Int? = null,
    val isAdult: Boolean? = null,
    val seasonYear: Int? = null,
    val coverImage: AniListCoverImage? = null,
    val genres: List<String>? = emptyList(),
    val studios: AniListStudioConnection? = null
)

data class AniListTitle(
    val romaji: String? = null,
    val english: String? = null,
    val native: String? = null
)

data class AniListCoverImage(
    val extraLarge: String? = null,
    val large: String? = null,
    val medium: String? = null
)

data class AniListStudioConnection(
    val nodes: List<NamedResourceDto>? = emptyList()
)

data class TranslationResponse(
    val responseData: TranslationData? = null,
    val responseStatus: Int? = null
)

data class TranslationData(
    val translatedText: String? = null
)
