package com.example.cinesuggest.data.repository

import com.example.cinesuggest.data.local.dao.FavouriteMovieDao
import com.example.cinesuggest.data.mapper.toDomain
import com.example.cinesuggest.data.mapper.toEntity
import com.example.cinesuggest.data.remote.ApiService
import com.example.cinesuggest.data.remote.dto.FavoriteRequest
import com.example.cinesuggest.data.remote.dto.RatingRequest
import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val movieDao : FavouriteMovieDao
): MovieRepository {


    private suspend fun <T> safeApiCall(apiCall : suspend () -> T) : Result<T> {
        return try {
            Result.success(apiCall.invoke())
        } catch (e : Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    override suspend fun getRecommendation(userId: Int): Result<List<Movie>> =
        safeApiCall { apiService.getRecommendation(userId = userId).recommendations.map { movieDto -> movieDto.toDomain() } }

    override suspend fun getAllMovies(): Result<List<Movie>> =
        safeApiCall { apiService.getAllMovies().movies.map {movieDto -> movieDto.toDomain() } }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> =
        safeApiCall { apiService.getMovieDetail(movieId = movieId).toDomain() }

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

    override fun getFavoritesCache(): Flow<List<FavoriteMovie>> =
        movieDao.getAllFavorites().map {
            it.map { entity -> entity.toDomain() }
        }


    override suspend fun addFavoriteToCache(movieDetail: MovieDetail) {
        movieDao.insertFavourite(movieDetail.toEntity())
    }

    override suspend fun removeFavoriteFromCache(movieId: Int) {
        movieDao.deleteFavourite(movieId = movieId)
    }

    override suspend fun getFavouriteId(favoriteId: Int) : Boolean =
        movieDao.getFavoriteId(favoriteId = favoriteId)


}