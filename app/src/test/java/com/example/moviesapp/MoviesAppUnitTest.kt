package com.example.moviesapp

import com.example.moviesapp.data.model.Genre
import com.example.moviesapp.data.model.GenreHelper
import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.model.MovieDetail
import com.example.moviesapp.data.repository.FavoritesRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MoviesAppUnitTest {

    @Test
    fun movie_posterAndBackdropUrls_areConstructedCorrectly() {
        val movie = Movie(
            id = 101,
            title = "Dune: Part Two",
            posterPath = "/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg",
            backdropPath = "/xOMo8BRK7PfcJv9JCnx7s520DRq.jpg",
            overview = "Paul Atreides unites with Chani and the Fremen...",
            voteAverage = 8.169,
            releaseDate = "2024-02-27",
            genreIds = listOf(878, 12)
        )

        assertEquals("https://image.tmdb.org/t/p/w500/8b8R8l88Qje9dn9OE8PY05Nxl1X.jpg", movie.getFullPosterUrl())
        assertEquals("https://image.tmdb.org/t/p/w780/xOMo8BRK7PfcJv9JCnx7s520DRq.jpg", movie.getFullBackdropUrl())
        assertEquals("8.2", movie.getFormattedRating())
        assertEquals("2024", movie.getReleaseYear())
    }

    @Test
    fun genreHelper_mapsKnownTmdbIdsCorrectly() {
        assertEquals("Action", GenreHelper.getGenreName(28))
        assertEquals("Sci-Fi", GenreHelper.getGenreName(878))
        assertEquals("Animation", GenreHelper.getGenreName(16))
        assertEquals(28, GenreHelper.getGenreIdByName("Action"))
        assertEquals(878, GenreHelper.getGenreIdByName("Sci-Fi"))
    }

    @Test
    fun favoritesRepository_addRemoveToggle_worksCorrectly() {
        val testMovie = Movie(
            id = 9999,
            title = "Test Cinema Film",
            voteAverage = 7.5
        )

        // Ensure clean initial state for this test ID
        FavoritesRepository.removeFavorite(testMovie.id)
        assertFalse(FavoritesRepository.isFavorite(testMovie.id))

        // Add
        FavoritesRepository.addFavorite(testMovie)
        assertTrue(FavoritesRepository.isFavorite(testMovie.id))

        // Toggle (should remove)
        FavoritesRepository.toggleFavorite(testMovie)
        assertFalse(FavoritesRepository.isFavorite(testMovie.id))

        // Toggle again (should add)
        FavoritesRepository.toggleFavorite(testMovie)
        assertTrue(FavoritesRepository.isFavorite(testMovie.id))

        // Cleanup
        FavoritesRepository.removeFavorite(testMovie.id)
        assertFalse(FavoritesRepository.isFavorite(testMovie.id))
    }

    @Test
    fun movieDetail_toMovie_conversionWorks() {
        val detail = MovieDetail(
            id = 550,
            title = "Fight Club",
            posterPath = "/pB8BM7pdSp6B6Ih7QZ4DrQ3PmJK.jpg",
            backdropPath = "/hZkgoQYus5vegHoetLkCJzb17zJ.jpg",
            overview = "An insomniac office worker and a devil-may-care soap maker...",
            voteAverage = 8.441,
            voteCount = 28000,
            releaseDate = "1999-10-15",
            runtime = 139,
            tagline = "Mischief. Mayhem. Soap.",
            genres = listOf(Genre(18, "Drama")),
            status = "Released"
        )

        val movie = detail.toMovie()
        assertEquals(550, movie.id)
        assertEquals("Fight Club", movie.title)
        assertEquals(listOf(18), movie.genreIds)
        assertEquals("2h 19m", detail.getFormattedRuntime())
        assertEquals("1999", detail.getReleaseYear())
    }
}
