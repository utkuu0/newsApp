package com.example.technews

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.technews.data.preferences.PreferencesManager
import com.example.technews.domain.repository.NewsRepository
import com.example.technews.presentation.navigation.NavGraph
import com.example.technews.ui.theme.TechNewsTheme
import com.example.technews.util.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferencesManager: PreferencesManager

    @Inject lateinit var repository: NewsRepository

    override fun attachBaseContext(newBase: Context) {
        // Get saved language synchronously
        val language = runBlocking {
            try {
                newBase.getSharedPreferences("settings_prefs", MODE_PRIVATE)
                    .getString("app_language", "system") ?: "system"
            } catch (_: Exception) {
                "system"
            }
        }
        super.attachBaseContext(LocaleHelper.setLocale(newBase, language))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Update last opened time
        lifecycleScope.launch { preferencesManager.updateLastOpenedTime() }

        setContent {
            val isDarkMode by preferencesManager.isDarkMode.collectAsState(initial = false)

            TechNewsTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    repository = repository,
                    onLanguageChange = { newLanguage ->
                        lifecycleScope.launch {
                            preferencesManager.setAppLanguage(newLanguage)
                            // Save to SharedPreferences for attachBaseContext
                            getSharedPreferences("settings_prefs", MODE_PRIVATE).edit {
                                putString("app_language", newLanguage)
                            }
                            // Recreate activity to apply new locale
                            recreate()
                        }
                    }
                )
            }
        }
    }
}
