package com.example.technews.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.technews.R

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    @StringRes val labelRes: Int
) {
    data object Home : BottomNavItem(
        route = Screen.NewsList.route,
        icon = Icons.Default.Home,
        labelRes = R.string.nav_home
    )

    data object Saved : BottomNavItem(
        route = Screen.SavedArticles.route,
        icon = Icons.Default.Bookmark,
        labelRes = R.string.nav_saved
    )

    data object Settings : BottomNavItem(
        route = Screen.Settings.route,
        icon = Icons.Default.Settings,
        labelRes = R.string.nav_settings
    )

    companion object {
        val items = listOf(Home, Saved, Settings)
    }
}

