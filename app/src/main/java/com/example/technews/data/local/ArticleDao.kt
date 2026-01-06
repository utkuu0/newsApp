package com.example.technews.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticleDao {

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun getAllArticles(): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    fun getArticlesByCategory(category: String): Flow<List<ArticleEntity>>

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    suspend fun getAllArticlesList(): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE category = :category ORDER BY publishedAt DESC")
    suspend fun getArticlesByCategoryList(category: String): List<ArticleEntity>

    @Query("SELECT * FROM articles WHERE url = :url")
    suspend fun getArticleByUrl(url: String): ArticleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles") suspend fun deleteAllArticles()

    @Query("DELETE FROM articles WHERE category = :category")
    suspend fun deleteArticlesByCategory(category: String)

    @Query("DELETE FROM articles WHERE cachedAt < :timestamp")
    suspend fun deleteOldArticles(timestamp: Long)

    // Kaydedilen makaleler için yeni metodlar
    @Query("SELECT * FROM articles WHERE isSaved = 1 ORDER BY publishedAt DESC")
    fun getSavedArticles(): Flow<List<ArticleEntity>>

    @Query("UPDATE articles SET isSaved = :isSaved WHERE url = :url")
    suspend fun updateSavedStatus(url: String, isSaved: Boolean)

    @Query("SELECT isSaved FROM articles WHERE url = :url")
    suspend fun isArticleSaved(url: String): Boolean?
}
