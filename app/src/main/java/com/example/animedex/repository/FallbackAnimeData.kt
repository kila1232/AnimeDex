package com.example.animedex.repository

import com.example.animedex.model.AnimeDto
import com.example.animedex.model.AnimeImageFormat
import com.example.animedex.model.AnimeImages
import com.example.animedex.model.NamedResourceDto

object FallbackAnimeData {
    private val animeList = listOf(
        AnimeDto(
            id = 5114,
            title = "Fullmetal Alchemist: Brotherhood",
            synopsis = "Dua bersaudara berpetualang mencari Philosopher's Stone setelah eksperimen alkimia mereka gagal. Data ini dipakai sementara saat server AniList sedang sibuk.",
            episodes = 64,
            status = "Finished Airing",
            score = 9.1,
            rating = "R - 17+",
            year = 2009,
            images = animeImage("https://cdn.myanimelist.net/images/anime/1208/94745l.jpg"),
            genres = genres("Action", "Adventure", "Drama", "Fantasy"),
            studios = studios("Bones")
        ),
        AnimeDto(
            id = 16498,
            title = "Shingeki no Kyojin",
            titleEnglish = "Attack on Titan",
            synopsis = "Manusia bertahan hidup di balik tembok raksasa dari ancaman Titan. Data ini dipakai sementara saat server AniList sedang sibuk.",
            episodes = 25,
            status = "Finished Airing",
            score = 8.6,
            rating = "R - 17+",
            year = 2013,
            images = animeImage("https://cdn.myanimelist.net/images/anime/10/47347l.jpg"),
            genres = genres("Action", "Drama", "Suspense"),
            studios = studios("Wit Studio")
        ),
        AnimeDto(
            id = 1535,
            title = "Death Note",
            synopsis = "Light Yagami menemukan buku misterius yang dapat mengakhiri hidup seseorang hanya dengan menulis namanya. Data ini dipakai sementara saat server AniList sedang sibuk.",
            episodes = 37,
            status = "Finished Airing",
            score = 8.6,
            rating = "R - 17+",
            year = 2006,
            images = animeImage("https://cdn.myanimelist.net/images/anime/9/9453l.jpg"),
            genres = genres("Supernatural", "Suspense"),
            studios = studios("Madhouse")
        ),
        AnimeDto(
            id = 21,
            title = "One Piece",
            synopsis = "Monkey D. Luffy berlayar bersama kru Topi Jerami untuk menemukan harta karun legendaris. Data ini dipakai sementara saat server AniList sedang sibuk.",
            episodes = null,
            status = "Currently Airing",
            score = 8.7,
            rating = "PG-13",
            year = 1999,
            images = animeImage("https://cdn.myanimelist.net/images/anime/6/73245l.jpg"),
            genres = genres("Action", "Adventure", "Fantasy"),
            studios = studios("Toei Animation")
        ),
        AnimeDto(
            id = 38000,
            title = "Kimetsu no Yaiba",
            titleEnglish = "Demon Slayer",
            synopsis = "Tanjiro Kamado menjadi pemburu iblis untuk menyelamatkan adiknya dan membalas tragedi keluarganya. Data ini dipakai sementara saat server AniList sedang sibuk.",
            episodes = 26,
            status = "Finished Airing",
            score = 8.4,
            rating = "R - 17+",
            year = 2019,
            images = animeImage("https://cdn.myanimelist.net/images/anime/1286/99889l.jpg"),
            genres = genres("Action", "Fantasy"),
            studios = studios("ufotable")
        ),
        AnimeDto(
            id = 9253,
            title = "Steins;Gate",
            synopsis = "Eksperimen pesan lintas waktu membawa Rintarou Okabe dan teman-temannya ke konsekuensi yang tidak mereka duga. Data ini dipakai sementara saat server AniList sedang sibuk.",
            episodes = 24,
            status = "Finished Airing",
            score = 9.0,
            rating = "PG-13",
            year = 2011,
            images = animeImage("https://cdn.myanimelist.net/images/anime/5/73199l.jpg"),
            genres = genres("Drama", "Sci-Fi", "Suspense"),
            studios = studios("White Fox")
        )
    )

    fun list(query: String?): List<AnimeDto> {
        val keyword = query?.trim().orEmpty()
        if (keyword.isBlank()) return animeList

        return animeList.filter { anime ->
            anime.displayTitle.contains(keyword, ignoreCase = true) ||
                anime.titleEnglish.orEmpty().contains(keyword, ignoreCase = true) ||
                anime.genreText.contains(keyword, ignoreCase = true)
        }
    }

    fun detail(animeId: Int): AnimeDto? = animeList.firstOrNull { it.id == animeId }

    private fun animeImage(url: String): AnimeImages = AnimeImages(
        jpg = AnimeImageFormat(imageUrl = url, largeImageUrl = url),
        webp = null
    )

    private fun genres(vararg names: String): List<NamedResourceDto> =
        names.mapIndexed { index, name -> NamedResourceDto(id = index + 1, name = name) }

    private fun studios(vararg names: String): List<NamedResourceDto> =
        names.mapIndexed { index, name -> NamedResourceDto(id = index + 1, name = name) }
}
