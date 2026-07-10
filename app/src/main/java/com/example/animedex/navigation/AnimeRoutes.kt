package com.example.animedex.navigation

object AnimeRoutes {
    const val HOME = "home"
    const val DETAIL = "detail/{animeId}"

    fun detail(animeId: Int): String = "detail/$animeId"
}
