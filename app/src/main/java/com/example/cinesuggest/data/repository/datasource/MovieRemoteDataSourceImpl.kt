package com.example.cinesuggest.data.repository.datasource

import com.example.cinesuggest.data.mapper.toDomain
import com.example.cinesuggest.data.remote.ApiService
import com.example.cinesuggest.data.remote.dto.FavoriteRequest
import com.example.cinesuggest.data.remote.dto.RatingRequest
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.domain.repository.datasource.MovieRemoteDataSource
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


class MovieRemoteDataSourceImpl @Inject constructor(
    private val apiService: ApiService
) : MovieRemoteDataSource {

    private suspend fun <T> safeApiCall(apiCall : suspend () -> T) : Result<T> {
        return try {
            Result.success(apiCall.invoke())
        } catch (e : Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    override suspend fun getRecommendation(userId: Int): Result<List<Movie>> =
        safeApiCall { apiService.getRecommendation(userId = userId).recommendations.map { movieDto -> movieDto.toDomain()} }


    override suspend fun getAllMovies(): Result<List<Movie>> =
        safeApiCall { apiService.getAllMovies().movies.map { movieDto -> movieDto.toDomain() } }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> =
        safeApiCall { apiService.getMovieDetail(movieId = movieId).toDomain() }

    override suspend fun rateMovie(
        userId: Int,
        movieId: Int,
        rating: Int
    ) : Result<Unit> =
        safeApiCall {
            apiService.rateMovie(RatingRequest(userId = userId , movieId = movieId , rating = rating))
        }

    override suspend fun toggleFavorite(
        userId: Int,
        movieId: Int
    ): Result<Unit> =
        safeApiCall {
            apiService.toggleFavorite(FavoriteRequest(userId = userId , movieId = movieId))
        }

}