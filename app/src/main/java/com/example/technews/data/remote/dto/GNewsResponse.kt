package com.example.technews.data.remote.dto

import com.google.gson.annotations.SerializedName

data class GNewsResponse(
    @SerializedName("totalArticles")
    val totalArticles: Int,
    @SerializedName("articles")
    val articles: List<GNewsArticleDto>
)

data class GNewsArticleDto(
    @SerializedName("title")
    val title: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("content")
    val content: String?,
    @SerializedName("url")
    val url: String?,
    @SerializedName("image")
    val image: String?,
    @SerializedName("publishedAt")
    val publishedAt: String?,
    @SerializedName("source")
    val source: GNewsSourceDto?
)

data class GNewsSourceDto(
    @SerializedName("name")
    val name: String?,
    @SerializedName("url")
    val url: String?
)

