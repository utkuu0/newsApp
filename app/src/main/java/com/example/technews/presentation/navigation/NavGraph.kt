package com.example.technews.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.technews.domain.repository.NewsRepository
import com.example.technews.presentation.detail.ArticleDetailScreen
import com.example.technews.presentation.newslist.NewsListScreen
import com.example.technews.presentation.saved.SavedArticlesScreen
import com.example.technews.presentation.settings.SettingsScreen

@Composable
fun NavGraph(navController: NavHostController, repository: NewsRepository) {
    NavHost(navController = navController, startDestination = Screen.NewsList.route) {
        composable(route = Screen.NewsList.route) {
            NewsListScreen(
                    onArticleClick = { articleUrl ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleUrl))
                    },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) },
                    onSavedClick = { navController.navigate(Screen.SavedArticles.route) }
            )
        }

        composable(
                route = Screen.ArticleDetail.route,
                arguments = listOf(navArgument("articleUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val articleUrl = backStackEntry.arguments?.getString("articleUrl") ?: ""
            ArticleDetailScreen(
                    articleUrl = articleUrl,
                    repository = repository,
                    onBackClick = { navController.popBackStack() }
            )
        }

        composable(route = Screen.Settings.route) {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }

        composable(route = Screen.SavedArticles.route) {
            SavedArticlesScreen(
                    onBackClick = { navController.popBackStack() },
                    onArticleClick = { articleUrl ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleUrl))
                    }
            )
        }
    }
}
