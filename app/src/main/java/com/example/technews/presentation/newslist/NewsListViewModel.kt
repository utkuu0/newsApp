package com.example.technews.presentation.newslist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.technews.domain.model.NewsCategory
import com.example.technews.domain.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NewsListViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(NewsListState())
    val state: StateFlow<NewsListState> = _state.asStateFlow()

    private var observeJob: Job? = null

    init {
        selectCategory(NewsCategory.TECHNOLOGY)
    }

    fun selectCategory(category: NewsCategory) {
        _state.update { it.copy(selectedCategory = category, isLoading = true) }
        observeArticles(category)
        refreshNews(category)
    }

    private fun observeArticles(category: NewsCategory) {
        observeJob?.cancel()
        observeJob =
                repository
                        .getArticlesByCategory(category)
                        .onEach { articles ->
                            _state.update { it.copy(articles = articles, isLoading = false) }
                        }
                        .launchIn(viewModelScope)
    }

    fun refreshNews(category: NewsCategory = _state.value.selectedCategory) {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true, error = null) }

            repository
                    .refreshArticles(category)
                    .onSuccess { _state.update { it.copy(isRefreshing = false, error = null) } }
                    .onFailure { exception ->
                        _state.update {
                            it.copy(
                                    isRefreshing = false,
                                    error = exception.message ?: "Bir hata oluştu"
                            )
                        }
                    }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
