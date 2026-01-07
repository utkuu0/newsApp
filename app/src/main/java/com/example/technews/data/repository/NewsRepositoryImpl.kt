package com.example.technews.data.repository

import com.example.technews.BuildConfig
import com.example.technews.data.local.ArticleDao
import com.example.technews.data.mapper.toDomain
import com.example.technews.data.mapper.toDomainList
import com.example.technews.data.mapper.toGNewsEntityList
import com.example.technews.data.remote.GNewsApiService
import com.example.technews.domain.model.Article
import com.example.technews.domain.model.NewsCategory
import com.example.technews.domain.repository.NewsRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class NewsRepositoryImpl
@Inject
constructor(
    private val gNewsApi: GNewsApiService,
    private val dao: ArticleDao
) : NewsRepository {

    override fun getArticles(): Flow<List<Article>> {
        return dao.getAllArticles().map { entities -> entities.toDomainList() }
    }

    override fun getArticlesByCategory(category: NewsCategory): Flow<List<Article>> {
        return dao.getArticlesByCategory(category.apiName).map { entities ->
            entities.toDomainList()
        }
    }

    override fun getSavedArticles(): Flow<List<Article>> {
        return dao.getSavedArticles().map { entities -> entities.toDomainList() }
    }

    override suspend fun refreshArticles(category: NewsCategory): Result<Unit> {
        return try {
            val gNewsCategory = mapCategoryToGNews(category)
            val response = gNewsApi.getTopHeadlines(
                category = gNewsCategory,
                language = "en",
                max = 10,
                apiKey = BuildConfig.GNEWS_API_KEY
            )

            if (response.articles.isNotEmpty()) {
                val entities = response.articles.toGNewsEntityList(category)
                dao.deleteArticlesByCategory(category.apiName)
                dao.insertArticles(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("No articles found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapCategoryToGNews(category: NewsCategory): String {
        return when (category) {
            NewsCategory.GENERAL -> "general"
            NewsCategory.TECHNOLOGY -> "technology"
            NewsCategory.BUSINESS -> "business"
            NewsCategory.SCIENCE -> "science"
            NewsCategory.HEALTH -> "health"
            NewsCategory.SPORTS -> "sports"
            NewsCategory.ENTERTAINMENT -> "entertainment"
        }
    }

    override suspend fun refreshAllCategories(): Result<Unit> {
        return try {
            NewsCategory.entries.forEach { category ->
                val gNewsCategory = mapCategoryToGNews(category)
                val response = gNewsApi.getTopHeadlines(
                    category = gNewsCategory,
                    language = "en",
                    max = 10,
                    apiKey = BuildConfig.GNEWS_API_KEY
                )

                if (response.articles.isNotEmpty()) {
                    val entities = response.articles.toGNewsEntityList(category)
                    dao.deleteArticlesByCategory(category.apiName)
                    dao.insertArticles(entities)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArticleByUrl(url: String): Article? {
        return dao.getArticleByUrl(url)?.toDomain()
    }

    override suspend fun toggleSaveArticle(url: String) {
        val currentStatus = dao.isArticleSaved(url) ?: false
        dao.updateSavedStatus(url, !currentStatus)
    }

    override suspend fun isArticleSaved(url: String): Boolean {
        return dao.isArticleSaved(url) ?: false
    }
}
