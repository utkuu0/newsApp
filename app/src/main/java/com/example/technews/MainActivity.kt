package com.example.technews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.technews.data.preferences.PreferencesManager
import com.example.technews.domain.repository.NewsRepository
import com.example.technews.presentation.navigation.NavGraph
import com.example.technews.ui.theme.TechNewsTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var preferencesManager: PreferencesManager

    @Inject lateinit var repository: NewsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Update last opened time
        lifecycleScope.launch { preferencesManager.updateLastOpenedTime() }

        setContent {
            val isDarkMode by preferencesManager.isDarkMode.collectAsState(initial = false)

            TechNewsTheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                NavGraph(navController = navController, repository = repository)
            }
        }
    }
}
