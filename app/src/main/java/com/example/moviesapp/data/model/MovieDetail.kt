package com.example.moviesapp.data.model

import com.google.gson.annotations.SerializedName

data class MovieDetail(
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
    @SerializedName("vote_count")
    val voteCount: Int = 0,
    @SerializedName("release_date")
    val releaseDate: String? = null,
    @SerializedName("runtime")
    val runtime: Int? = null,
    @SerializedName("tagline")
    val tagline: String? = null,
    @SerializedName("genres")
    val genres: List<Genre> = emptyList(),
    @SerializedName("status")
    val status: String? = null
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

    fun getFormattedRuntime(): String {
        val totalMinutes = runtime ?: return ""
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
    }

    fun toMovie(): Movie {
        return Movie(
            id = id,
            title = title,
            posterPath = posterPath,
            backdropPath = backdropPath,
            overview = overview,
            voteAverage = voteAverage,
            releaseDate = releaseDate,
            genreIds = genres.map { it.id }
        )
    }
}
