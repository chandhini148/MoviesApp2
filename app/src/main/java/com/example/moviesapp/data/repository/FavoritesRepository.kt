package com.example.moviesapp.data.repository

import com.example.moviesapp.data.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map

object FavoritesRepository {

    private val _favorites = MutableStateFlow<List<Movie>>(emptyList())
    val favorites: StateFlow<List<Movie>> = _favorites.asStateFlow()

    fun isFavorite(movieId: Int): Boolean {
        return _favorites.value.any { it.id == movieId }
    }

    fun isFavoriteFlow(movieId: Int) = _favorites.map { list ->
        list.any { it.id == movieId }
    }

    fun toggleFavorite(movie: Movie) {
        val current = _favorites.value.toMutableList()
        val existingIndex = current.indexOfFirst { it.id == movie.id }
        if (existingIndex >= 0) {
            current.removeAt(existingIndex)
        } else {
            current.add(0, movie)
        }
        _favorites.value = current
    }

    fun addFavorite(movie: Movie) {
        if (!isFavorite(movie.id)) {
            _favorites.value = listOf(movie) + _favorites.value
        }
    }

    fun removeFavorite(movieId: Int) {
        _favorites.value = _favorites.value.filterNot { it.id == movieId }
    }
}
