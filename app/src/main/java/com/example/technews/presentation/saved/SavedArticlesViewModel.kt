package com.example.technews.presentation.saved

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technews.R
import com.example.technews.domain.model.Article
import com.example.technews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SavedArticlesState(
        val articles: List<Article> = emptyList(),
        val isLoading: Boolean = true
)

@HiltViewModel
class SavedArticlesViewModel @Inject constructor(
        private val repository: NewsRepository,
        @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(SavedArticlesState())
    val state: StateFlow<SavedArticlesState> = _state.asStateFlow()

    private val _snackbarEvent = MutableSharedFlow<Int>()
    val snackbarEvent: SharedFlow<Int> = _snackbarEvent.asSharedFlow()

    init {
        loadSavedArticles()
    }

    private fun loadSavedArticles() {
        viewModelScope.launch {
            repository.getSavedArticles().collect { articles ->
                _state.value = _state.value.copy(articles = articles, isLoading = false)
            }
        }
    }

    fun removeFromSaved(url: String) {
        viewModelScope.launch {
            repository.toggleSaveArticle(url)
            _snackbarEvent.emit(R.string.news_removed)
        }
    }

    fun getSnackbarMessage(resId: Int): String {
        return context.getString(resId)
    }
}
