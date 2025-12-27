package com.example.technews.data.repository

import com.example.technews.BuildConfig
import com.example.technews.data.local.ArticleDao
import com.example.technews.data.mapper.toDomain
import com.example.technews.data.mapper.toDomainList
import com.example.technews.data.mapper.toEntityList
import com.example.technews.data.remote.NewsApiService
import com.example.technews.domain.model.Article
import com.example.technews.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val api: NewsApiService,
    private val dao: ArticleDao
) : NewsRepository {

    override fun getArticles(): Flow<List<Article>> {
        return dao.getAllArticles().map { entities ->
            entities.toDomainList()
        }
    }

    override suspend fun refreshArticles(): Result<Unit> {
        return try {
            val response = api.getTopHeadlines(
                category = "technology",
                language = "en",
                apiKey = BuildConfig.NEWS_API_KEY
            )
            
            if (response.status == "ok") {
                val entities = response.articles.toEntityList()
                // Clear old articles and insert new ones
                dao.deleteAllArticles()
                dao.insertArticles(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArticleByUrl(url: String): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }
}
