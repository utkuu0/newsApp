package com.example.technews.util

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "tr" -> Locale("tr")
            "en" -> Locale("en")
            "system" -> getSystemLocale()
            else -> getSystemLocale()
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
        }

        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun getSystemLocale(): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList.getDefault()[0]
        } else {
            @Suppress("DEPRECATION")
            Locale.getDefault()
        }
    }

    fun getLanguageDisplayName(languageCode: String): String {
        return when (languageCode) {
            "tr" -> "Türkçe"
            "en" -> "English"
            "system" -> "Sistem"
            else -> languageCode
        }
    }
}

