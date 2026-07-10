package com.example.animedex.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.animedex.ui.screen.DetailScreen
import com.example.animedex.ui.screen.HomeScreen

@Composable
fun AnimeDexNavHost() {
    // NavController mengatur perpindahan Home ke Detail.
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = AnimeRoutes.HOME
    ) {
        composable(AnimeRoutes.HOME) {
            HomeScreen(
                onAnimeClick = { animeId ->
                    navController.navigate(AnimeRoutes.detail(animeId))
                }
            )
        }

        composable(
            route = AnimeRoutes.DETAIL,
            arguments = listOf(navArgument("animeId") { type = NavType.IntType })
        ) { backStackEntry ->
            val animeId = backStackEntry.arguments?.getInt("animeId") ?: return@composable
            DetailScreen(
                animeId = animeId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
