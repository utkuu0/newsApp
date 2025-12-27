package com.example.technews.domain.repository

import com.example.technews.domain.model.Article
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getArticles(): Flow<List<Article>>
    suspend fun refreshArticles(): Result<Unit>
    suspend fun getArticleByUrl(url: String): Article?
}
