package com.example.moviesapp.data.model

import com.google.gson.annotations.SerializedName

data class Genre(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String
)

object GenreHelper {
    private val genreMap = mapOf(
        28 to "Action",
        12 to "Adventure",
        16 to "Animation",
        35 to "Comedy",
        80 to "Crime",
        99 to "Documentary",
        18 to "Drama",
        10751 to "Family",
        14 to "Fantasy",
        36 to "History",
        27 to "Horror",
        10402 to "Music",
        9648 to "Mystery",
        10749 to "Romance",
        878 to "Sci-Fi",
        10770 to "TV Movie",
        53 to "Thriller",
        10752 to "War",
        37 to "Western"
    )

    fun getGenreName(id: Int): String {
        return genreMap[id] ?: "Movie"
    }

    fun getGenreNames(ids: List<Int>?): List<String> {
        return ids?.mapNotNull { genreMap[it] } ?: emptyList()
    }

    val defaultCategories = listOf(
        "All",
        "Action",
        "Adventure",
        "Animation",
        "Comedy",
        "Drama",
        "Fantasy",
        "Horror",
        "Sci-Fi",
        "Thriller"
    )

    fun getGenreIdByName(name: String): Int? {
        return genreMap.entries.firstOrNull { it.value.equals(name, ignoreCase = true) }?.key
    }
}
