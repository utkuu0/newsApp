package com.example.technews.data.mapper

import com.example.technews.data.local.ArticleEntity
import com.example.technews.data.remote.dto.ArticleDto
import com.example.technews.domain.model.Article
import com.example.technews.domain.model.NewsCategory

fun ArticleDto.toEntity(category: NewsCategory = NewsCategory.GENERAL): ArticleEntity? {
    // Skip articles without title or url
    if (title.isNullOrBlank() || url.isNullOrBlank()) return null

    return ArticleEntity(
            url = url,
            sourceName = source?.name,
            author = author,
            title = title,
            description = description,
            urlToImage = urlToImage,
            publishedAt = publishedAt,
            content = content,
            category = category.apiName
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
            url = url,
            sourceName = sourceName ?: "Unknown",
            author = author ?: "Unknown",
            title = title,
            description = description ?: "",
            urlToImage = urlToImage,
            publishedAt = publishedAt ?: "",
            content = content ?: "",
            category = NewsCategory.fromApiName(category)
    )
}

fun List<ArticleDto>.toEntityList(
        category: NewsCategory = NewsCategory.GENERAL
): List<ArticleEntity> {
    return mapNotNull { it.toEntity(category) }
}

fun List<ArticleEntity>.toDomainList(): List<Article> {
    return map { it.toDomain() }
}
