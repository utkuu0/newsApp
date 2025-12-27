package com.example.technews.presentation.navigation

sealed class Screen(val route: String) {
    object NewsList : Screen("news_list")
    object ArticleDetail : Screen("article_detail/{articleUrl}") {
        fun createRoute(articleUrl: String): String {
            return "article_detail/${java.net.URLEncoder.encode(articleUrl, "UTF-8")}"
        }
    }
    object Settings : Screen("settings")
}
