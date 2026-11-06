package com.example.moviesapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviesapp.data.model.MovieDetail
import com.example.moviesapp.data.repository.FavoritesRepository
import com.example.moviesapp.data.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

sealed interface DetailUiState {
    object Loading : DetailUiState
    data class Success(
        val movie: MovieDetail,
        val isFavorite: Boolean
    ) : DetailUiState
    data class Error(val message: String) : DetailUiState
}

class DetailViewModel(
    private val repository: MovieRepository = MovieRepository(),
    private val favoritesRepository: FavoritesRepository = FavoritesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private var currentMovieId: Int = -1

    fun loadMovie(movieId: Int) {
        currentMovieId = movieId
        viewModelScope.launch {
            _uiState.value = DetailUiState.Loading

            val result = repository.getMovieDetails(movieId)
            if (result.isSuccess) {
                val movie = result.getOrThrow()
                // Monitor favorite state reactively
                favoritesRepository.isFavoriteFlow(movieId).collectLatest { isFav ->
                    _uiState.value = DetailUiState.Success(
                        movie = movie,
                        isFavorite = isFav
                    )
                }
            } else {
                _uiState.value = DetailUiState.Error(
                    result.exceptionOrNull()?.message ?: "Failed to load movie details"
                )
            }
        }
    }

    fun toggleFavorite() {
        val state = _uiState.value
        if (state is DetailUiState.Success) {
            favoritesRepository.toggleFavorite(state.movie.toMovie())
        }
    }
}
