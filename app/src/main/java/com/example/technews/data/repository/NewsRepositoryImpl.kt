package com.example.technews.data.repository

import com.example.technews.BuildConfig
import com.example.technews.data.local.ArticleDao
import com.example.technews.data.mapper.toDomain
import com.example.technews.data.mapper.toDomainList
import com.example.technews.data.mapper.toEntityList
import com.example.technews.data.remote.NewsApiService
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
constructor(private val api: NewsApiService, private val dao: ArticleDao) : NewsRepository {

    override fun getArticles(): Flow<List<Article>> {
        return dao.getAllArticles().map { entities -> entities.toDomainList() }
    }

    override fun getArticlesByCategory(category: NewsCategory): Flow<List<Article>> {
        return dao.getArticlesByCategory(category.apiName).map { entities ->
            entities.toDomainList()
        }
    }

    override suspend fun refreshArticles(category: NewsCategory): Result<Unit> {
        return try {
            val response =
                    api.getTopHeadlines(
                            category = category.apiName,
                            language = "en",
                            apiKey = BuildConfig.NEWS_API_KEY
                    )

            if (response.status == "ok") {
                val entities = response.articles.toEntityList(category)
                // Clear old articles for this category and insert new ones
                dao.deleteArticlesByCategory(category.apiName)
                dao.insertArticles(entities)
                Result.success(Unit)
            } else {
                Result.failure(Exception("API returned error status"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun refreshAllCategories(): Result<Unit> {
        return try {
            NewsCategory.entries.forEach { category ->
                val response =
                        api.getTopHeadlines(
                                category = category.apiName,
                                language = "en",
                                apiKey = BuildConfig.NEWS_API_KEY
                        )

                if (response.status == "ok") {
                    val entities = response.articles.toEntityList(category)
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
}
