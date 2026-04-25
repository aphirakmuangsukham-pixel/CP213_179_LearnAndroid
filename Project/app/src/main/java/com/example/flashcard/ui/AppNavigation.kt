package com.example.flashcard.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flashcard.ui.screens.CategoryDetailScreen
import com.example.flashcard.ui.screens.HomeScreen
import com.example.flashcard.ui.screens.StudyScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Settings : Screen("settings")
    object CategoryDetail : Screen("category_detail/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Int, categoryName: String) = "category_detail/$categoryId/$categoryName"
    }
    object Study : Screen("study/{categoryId}/{categoryName}") {
        fun createRoute(categoryId: Int, categoryName: String) = "study/$categoryId/$categoryName"
    }
    object ChallengeMenu : Screen("challenge_menu")
    object ChallengeGame : Screen("challenge_game/{mode}") {
        fun createRoute(mode: String) = "challenge_game/$mode"
    }
}

@Composable
fun AppNavigation(viewModel: FlashCardViewModel = viewModel()) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController, viewModel)
        }
        composable(Screen.Settings.route) {
            com.example.flashcard.ui.screens.SettingsScreen(navController, viewModel)
        }
        composable(Screen.ChallengeMenu.route) {
            com.example.flashcard.ui.screens.ChallengeMenuScreen(navController, viewModel)
        }
        composable(
            route = Screen.ChallengeGame.route,
            arguments = listOf(navArgument("mode") { type = NavType.StringType })
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString("mode") ?: "quiz"
            com.example.flashcard.ui.screens.ChallengeGameScreen(navController, viewModel, mode)
        }
        composable(
            route = Screen.CategoryDetail.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            CategoryDetailScreen(navController, viewModel, categoryId, categoryName)
        }
        composable(
            route = Screen.Study.route,
            arguments = listOf(
                navArgument("categoryId") { type = NavType.IntType },
                navArgument("categoryName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val categoryId = backStackEntry.arguments?.getInt("categoryId") ?: 0
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            StudyScreen(navController, viewModel, categoryId, categoryName)
        }
    }
}
