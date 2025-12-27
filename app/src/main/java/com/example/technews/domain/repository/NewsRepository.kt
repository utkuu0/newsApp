package com.example.technews.domain.repository

import com.example.technews.domain.model.Article
import com.example.technews.domain.model.NewsCategory
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getArticles(): Flow<List<Article>>
    fun getArticlesByCategory(category: NewsCategory): Flow<List<Article>>
    suspend fun refreshArticles(category: NewsCategory): Result<Unit>
    suspend fun refreshAllCategories(): Result<Unit>
    suspend fun getArticleByUrl(url: String): Article?
}
