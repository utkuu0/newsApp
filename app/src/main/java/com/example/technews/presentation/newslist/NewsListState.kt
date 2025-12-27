package com.example.technews.presentation.newslist

import com.example.technews.domain.model.Article

data class NewsListState(
        val articles: List<Article> = emptyList(),
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val error: String? = null
)
