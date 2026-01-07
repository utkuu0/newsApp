package com.example.technews.data.mapper

import com.example.technews.data.local.ArticleEntity
import com.example.technews.data.remote.dto.GNewsArticleDto
import com.example.technews.domain.model.Article
import com.example.technews.domain.model.NewsCategory

// HTML etiketlerini temizleyen yardımcı fonksiyon
private fun String?.stripHtmlTags(): String {
    if (this.isNullOrBlank()) return ""
    return this.replace(Regex("<[^>]*>"), "") // HTML etiketlerini kaldır
            .replace("&nbsp;", " ")
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace(Regex("\\s+"), " ") // Fazla boşlukları tek boşluğa çevir
            .trim()
}

fun ArticleEntity.toDomain(): Article {
    return Article(
            url = url,
            sourceName = sourceName ?: "Unknown",
            author = author ?: "Unknown",
            title = title,
            description = (description ?: "").stripHtmlTags(),
            urlToImage = urlToImage,
            publishedAt = publishedAt ?: "",
            content = (content ?: "").stripHtmlTags(),
            category = NewsCategory.fromApiName(category),
            isSaved = isSaved
    )
}

fun List<ArticleEntity>.toDomainList(): List<Article> {
    return map { it.toDomain() }
}

// GNews API Mappers
fun GNewsArticleDto.toEntity(category: NewsCategory = NewsCategory.GENERAL): ArticleEntity? {
    if (title.isNullOrBlank() || url.isNullOrBlank()) return null

    return ArticleEntity(
        url = url,
        sourceName = source?.name,
        author = source?.name, // GNews doesn't provide author, use source name
        title = title,
        description = description?.stripHtmlTags(),
        urlToImage = image,
        publishedAt = publishedAt,
        content = content?.stripHtmlTags(),
        category = category.apiName
    )
}

fun List<GNewsArticleDto>.toGNewsEntityList(
    category: NewsCategory = NewsCategory.GENERAL
): List<ArticleEntity> {
    return mapNotNull { it.toEntity(category) }
}


