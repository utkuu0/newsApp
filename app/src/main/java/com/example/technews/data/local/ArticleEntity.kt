package com.example.technews.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articles")
data class ArticleEntity(
        @PrimaryKey val url: String,
        val sourceName: String?,
        val author: String?,
        val title: String,
        val description: String?,
        val urlToImage: String?,
        val publishedAt: String?,
        val content: String?,
        val category: String = "general",
        val cachedAt: Long = System.currentTimeMillis(),
        val isSaved: Boolean = false
)
