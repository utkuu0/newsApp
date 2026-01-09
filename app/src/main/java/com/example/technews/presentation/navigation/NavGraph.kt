package com.example.technews.presentation.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.technews.domain.model.toComposeColor
import com.example.technews.domain.repository.NewsRepository
import com.example.technews.presentation.detail.ArticleDetailScreen
import com.example.technews.presentation.newslist.NewsListScreen
import com.example.technews.presentation.newslist.NewsListViewModel
import com.example.technews.presentation.saved.SavedArticlesScreen
import com.example.technews.presentation.settings.SettingsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: NewsRepository,
    onLanguageChange: (String) -> Unit = {}
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Refresh trigger for home screen
    var refreshTrigger by remember { mutableStateOf(0) }

    // Get category color from shared ViewModel
    val newsListViewModel: NewsListViewModel = hiltViewModel()
    val newsState by newsListViewModel.state.collectAsState()

    // Animate category color for navigation bar
    val categoryColor by animateColorAsState(
        targetValue = newsState.selectedCategory.primaryColor.toComposeColor(),
        animationSpec = tween(500),
        label = "navBarColor"
    )

    Scaffold(
        bottomBar = {
            // Hide bottom bar on detail screen
            if (currentRoute != Screen.ArticleDetail.route) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = null) },
                            label = { Text(stringResource(item.labelRes)) },
                            selected = currentRoute == item.route,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = categoryColor,
                                selectedTextColor = categoryColor,
                                indicatorColor = categoryColor.copy(alpha = 0.15f),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            onClick = {
                                if (item.route == BottomNavItem.Home.route && currentRoute == BottomNavItem.Home.route) {
                                    // Ana sayfadayken tekrar tıklanırsa yenile
                                    refreshTrigger++
                                    newsListViewModel.refreshNews()
                                } else {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.NewsList.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(route = Screen.NewsList.route) {
                NewsListScreen(
                    onArticleClick = { articleUrl ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleUrl))
                    },
                    refreshTrigger = refreshTrigger,
                    viewModel = newsListViewModel
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
                SettingsScreen(
                    categoryColor = categoryColor,
                    onLanguageChange = onLanguageChange
                )
            }

            composable(route = Screen.SavedArticles.route) {
                SavedArticlesScreen(
                    onArticleClick = { articleUrl ->
                        navController.navigate(Screen.ArticleDetail.createRoute(articleUrl))
                    },
                    categoryColor = categoryColor
                )
            }
        }
    }
}
