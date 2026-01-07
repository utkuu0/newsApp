package com.example.technews.data.remote

import com.example.technews.data.remote.dto.GNewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GNewsApiService {

    @GET("api/v4/top-headlines")
    suspend fun getTopHeadlines(
        @Query("category") category: String = "technology",
        @Query("lang") language: String = "en",
        @Query("country") country: String = "us",
        @Query("max") max: Int = 10,
        @Query("apikey") apiKey: String
    ): GNewsResponse

    @GET("api/v4/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("lang") language: String = "en",
        @Query("max") max: Int = 10,
        @Query("sortby") sortBy: String = "publishedAt",
        @Query("apikey") apiKey: String
    ): GNewsResponse

    companion object {
        const val BASE_URL = "https://gnews.io/"
    }
}

