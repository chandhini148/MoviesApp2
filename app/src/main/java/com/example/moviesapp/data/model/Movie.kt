package com.example.moviesapp.data.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("id")
    val id: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("poster_path")
    val posterPath: String? = null,
    @SerializedName("backdrop_path")
    val backdropPath: String? = null,
    @SerializedName("overview")
    val overview: String = "",
    @SerializedName("vote_average")
    val voteAverage: Double = 0.0,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null
) {
    fun getFullPosterUrl(): String? {
        return posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }
    }

    fun getFullBackdropUrl(): String? {
        return backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" }
    }

    fun getFormattedRating(): String {
        return String.format("%.1f", voteAverage)
    }

    fun getReleaseYear(): String {
        return releaseDate?.take(4) ?: ""
    }
}
