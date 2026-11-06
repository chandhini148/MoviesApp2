package com.example.moviesapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.repository.FavoritesRepository
import kotlinx.coroutines.flow.StateFlow

class FavoritesViewModel(
    private val repository: FavoritesRepository = FavoritesRepository
) : ViewModel() {

    val favorites: StateFlow<List<Movie>> = repository.favorites

    fun removeFavorite(movieId: Int) {
        repository.removeFavorite(movieId)
    }
}
