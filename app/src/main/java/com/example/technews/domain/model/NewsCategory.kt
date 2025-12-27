package com.example.technews.domain.model

import androidx.compose.ui.graphics.Color

/**
 * News categories supported by NewsAPI Each category has its own color theme for UI customization
 */
enum class NewsCategory(
        val apiName: String,
        val displayName: String,
        val icon: String, // Material icon name
        val primaryColor: Long,
        val gradientStart: Long,
        val gradientEnd: Long
) {
    TECHNOLOGY(
            apiName = "technology",
            displayName = "Teknoloji",
            icon = "PhoneAndroid",
            primaryColor = 0xFF6366F1,
            gradientStart = 0xFF6366F1,
            gradientEnd = 0xFF8B5CF6
    ),
    SPORTS(
            apiName = "sports",
            displayName = "Spor",
            icon = "SportsSoccer",
            primaryColor = 0xFF10B981,
            gradientStart = 0xFF10B981,
            gradientEnd = 0xFF34D399
    ),
    BUSINESS(
            apiName = "business",
            displayName = "Ekonomi",
            icon = "TrendingUp",
            primaryColor = 0xFFF59E0B,
            gradientStart = 0xFFF59E0B,
            gradientEnd = 0xFFFBBF24
    ),
    ENTERTAINMENT(
            apiName = "entertainment",
            displayName = "Eğlence",
            icon = "Movie",
            primaryColor = 0xFFEC4899,
            gradientStart = 0xFFEC4899,
            gradientEnd = 0xFFF472B6
    ),
    HEALTH(
            apiName = "health",
            displayName = "Sağlık",
            icon = "Favorite",
            primaryColor = 0xFFEF4444,
            gradientStart = 0xFFEF4444,
            gradientEnd = 0xFFF87171
    ),
    SCIENCE(
            apiName = "science",
            displayName = "Bilim",
            icon = "Science",
            primaryColor = 0xFF8B5CF6,
            gradientStart = 0xFF8B5CF6,
            gradientEnd = 0xFFA78BFA
    ),
    GENERAL(
            apiName = "general",
            displayName = "Genel",
            icon = "Newspaper",
            primaryColor = 0xFF3B82F6,
            gradientStart = 0xFF3B82F6,
            gradientEnd = 0xFF60A5FA
    );

    companion object {
        fun fromApiName(apiName: String): NewsCategory {
            return entries.find { it.apiName == apiName } ?: GENERAL
        }
    }
}

// Extension function to get Color from Long
fun Long.toComposeColor(): Color = Color(this)
