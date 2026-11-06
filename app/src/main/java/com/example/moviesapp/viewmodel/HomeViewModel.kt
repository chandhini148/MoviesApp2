package com.example.moviesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.data.model.GenreHelper
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val featuredMovies: List<Movie>,
        val trendingMovies: List<Movie>,
        val popularMovies: List<Movie>,
        val topRatedMovies: List<Movie>,
        val categoryMovies: List<Movie> = emptyList(),
        val selectedCategory: String = "All"
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            val trendingDeferred = async { repository.getTrendingMovies() }
            val popularDeferred = async { repository.getPopularMovies() }
            val topRatedDeferred = async { repository.getTopRatedMovies() }

            val trendingResult = trendingDeferred.await()
            val popularResult = popularDeferred.await()
            val topRatedResult = topRatedDeferred.await()

            if (trendingResult.isSuccess || popularResult.isSuccess || topRatedResult.isSuccess) {
                val trending = trendingResult.getOrDefault(emptyList())
                val popular = popularResult.getOrDefault(emptyList())
                val topRated = topRatedResult.getOrDefault(emptyList())

                val featured = (trending.takeIf { it.isNotEmpty() } ?: popular).take(6)

                _uiState.value = HomeUiState.Success(
                    featuredMovies = featured,
                    trendingMovies = trending,
                    popularMovies = popular,
                    topRatedMovies = topRated,
                    categoryMovies = popular,
                    selectedCategory = "All"
                )
            } else {
                val errorMsg = trendingResult.exceptionOrNull()?.message
                    ?: popularResult.exceptionOrNull()?.message
                    ?: "Failed to load movies. Please check your connection or API key."
                _uiState.value = HomeUiState.Error(errorMsg)
            }
        }
    }

    fun selectCategory(category: String) {
        val currentState = _uiState.value
        if (currentState !is HomeUiState.Success) return
        if (currentState.selectedCategory == category) return

        if (category == "All") {
            _uiState.value = currentState.copy(
                selectedCategory = "All",
                categoryMovies = currentState.popularMovies
            )
            return
        }

        val genreId = GenreHelper.getGenreIdByName(category)
        if (genreId == null) {
            _uiState.value = currentState.copy(selectedCategory = category)
            return
        }

        viewModelScope.launch {
            val result = repository.getMoviesByGenre(genreId)
            val currentStateNow = _uiState.value
            if (currentStateNow is HomeUiState.Success) {
                if (result.isSuccess) {
                    _uiState.value = currentStateNow.copy(
                        selectedCategory = category,
                        categoryMovies = result.getOrDefault(emptyList())
                    )
                } else {
                    _uiState.value = currentStateNow.copy(selectedCategory = category)
                }
            }
        }
    }
}
