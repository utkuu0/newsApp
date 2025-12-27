package com.example.technews.presentation.newslist

import com.example.technews.domain.model.Article
import com.example.technews.domain.model.NewsCategory

data class NewsListState(
        val articles: List<Article> = emptyList(),
        val isLoading: Boolean = true,
        val isRefreshing: Boolean = false,
        val error: String? = null,
        val selectedCategory: NewsCategory = NewsCategory.TECHNOLOGY,
        val categories: List<NewsCategory> = NewsCategory.entries
)
