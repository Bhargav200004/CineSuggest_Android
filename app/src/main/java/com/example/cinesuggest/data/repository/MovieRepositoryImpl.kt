package com.example.cinesuggest.data.repository

import com.example.cinesuggest.data.local.dao.FavouriteMovieDao
import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.mapper.toFavoriteMovieEntity
import com.example.cinesuggest.data.mapper.toMovieDetailDomain
import com.example.cinesuggest.data.remote.ApiService
import com.example.cinesuggest.data.remote.dto.FavoriteRequest
import com.example.cinesuggest.data.remote.dto.MovieDetailDto
import com.example.cinesuggest.data.remote.dto.MoviesResponseDto
import com.example.cinesuggest.data.remote.dto.RatingRequest
import com.example.cinesuggest.data.remote.dto.RecommendationResponseDto
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val movieDao : FavouriteMovieDao
): MovieRepository {


    private suspend fun <T> safeApiCall(apiCall : suspend () -> T) : Result<T> {
        return try {
            Result.success(apiCall.invoke())
        } catch (e : Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecommendation(userId: Int): Result<RecommendationResponseDto> =
        safeApiCall { apiService.getRecommendation(userId = userId) }

    override suspend fun getAllMovies(): Result<MoviesResponseDto> =
        safeApiCall {
            MoviesResponseDto(
                movies = apiService.getAllMovies()
            )
        }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> =
        safeApiCall { apiService.getMovieDetail(movieId = movieId).toMovieDetailDomain() }

    override suspend fun rateMovie(
        userId: Int,
        movieId: Int,
        rating: Int
    ): Result<Response<Unit>> =
        safeApiCall { apiService.rateMovie(RatingRequest(userId = userId , movieId = movieId , rating = rating)) }

    override suspend fun toggleFavorite(
        userId: Int,
        movieId: Int
    ): Result<Response<Unit>> =
        safeApiCall { apiService.toggleFavorite(FavoriteRequest(userId = userId , movieId = movieId)) }

    override fun getFavoritesCache(): Flow<List<FavoriteMovieEntity>> =
        movieDao.getAllFavorites()

    override suspend fun addFavoriteToCache(movie: MovieDetailDto) {
        movieDao.insertFavourite(movie.toFavoriteMovieEntity())
    }

    override suspend fun removeFavoriteFromCache(movieId: Int) {
        movieDao.deleteFavourite(movieId = movieId)
    }

    override suspend fun getFavouriteId(favoriteId: Int) : Boolean =
        movieDao.getFavoriteId(favoriteId = favoriteId)


}