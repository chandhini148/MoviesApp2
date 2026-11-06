package com.example.moviesapp.data.repository

import com.example.moviesapp.data.model.Movie
import com.example.moviesapp.data.model.MovieDetail
import com.example.moviesapp.data.remote.RetrofitClient
import com.example.moviesapp.data.remote.TmdbApiService
import retrofit2.HttpException
import java.io.IOException

class MovieRepository(
    private val apiService: TmdbApiService = RetrofitClient.apiService
) {

    private fun checkApiKeyConfigured(): Result<Unit>? {
        if (!RetrofitClient.isApiKeyConfigured()) {
            return Result.failure(
                IllegalStateException(
                    "TMDB API Key not found!\nPlease add 'TMDB_API_KEY=your_api_key' to local.properties and rebuild."
                )
            )
        }
        return null
    }

    private inline fun <T> safeApiCall(block: () -> T): Result<T> {
        checkApiKeyConfigured()?.let { return Result.failure(it.exceptionOrNull()!!) }

        return try {
            Result.success(block())
        } catch (e: HttpException) {
            val message = when (e.code()) {
                401 -> "Invalid TMDB API Key (401 Unauthorized). Please check your key in local.properties."
                404 -> "Requested movie data not found (404)."
                else -> "TMDB API Error: ${e.code()} ${e.message()}"
            }
            Result.failure(Exception(message, e))
        } catch (e: IOException) {
            Result.failure(Exception("Network error. Please check your internet connection.", e))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTrendingMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getTrendingMovies(page).results
        }
    }

    suspend fun getPopularMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getPopularMovies(page).results
        }
    }

    suspend fun getTopRatedMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getTopRatedMovies(page).results
        }
    }

    suspend fun getNowPlayingMovies(page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.getNowPlayingMovies(page).results
        }
    }

    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): Result<List<Movie>> {
        return safeApiCall {
            apiService.discoverMoviesByGenre(genreId, page).results
        }
    }

    suspend fun searchMovies(query: String, page: Int = 1): Result<List<Movie>> {
        if (query.isBlank()) return Result.success(emptyList())
        return safeApiCall {
            apiService.searchMovies(query, page).results
        }
    }

    suspend fun getMovieDetails(movieId: Int): Result<MovieDetail> {
        return safeApiCall {
            apiService.getMovieDetails(movieId)
        }
    }
}
