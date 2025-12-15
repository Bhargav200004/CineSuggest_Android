package com.example.cinesuggest.domain.repository

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.local.dao.FavouriteMovieDao
import com.example.cinesuggest.data.mapper.toFavoriteMovieEntity
import com.example.cinesuggest.data.remote.dto.FavoriteRequest
import com.example.cinesuggest.data.remote.dto.MovieDetail
import com.example.cinesuggest.data.remote.dto.MoviesResponse
import com.example.cinesuggest.data.remote.dto.RatingRequest
import com.example.cinesuggest.data.remote.dto.RecommendationResponse
import com.example.cinesuggest.data.remote.ApiService
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

interface MovieRepository {
    suspend fun getRecommendation(userId : Int) : Result<RecommendationResponse>
    suspend fun getAllMovies() : Result<MoviesResponse>
    suspend fun getMovieDetail(movieId : Int) : Result<MovieDetail>
    suspend fun rateMovie(userId : Int , movieId : Int , rating : Int ) : Result<Response<Unit>>
    suspend fun toggleFavorite(userId: Int, movieId: Int) : Result<Response<Unit>>
    fun getFavoritesCache() : Flow<List<FavoriteMovieEntity>>
    suspend fun getFavouriteId(favoriteId : Int) : Boolean
    suspend fun addFavoriteToCache(movie: MovieDetail)
    suspend fun removeFavoriteFromCache(movieId : Int)
}

@Singleton
class DefaultMovieRepository @Inject constructor(
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

    override suspend fun getRecommendation(userId: Int): Result<RecommendationResponse> =
        safeApiCall { apiService.getRecommendation(userId = userId) }

    override suspend fun getAllMovies(): Result<MoviesResponse> =
        safeApiCall {
            MoviesResponse(
                movies = apiService.getAllMovies()
            )
        }

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> =
        safeApiCall { apiService.getMovieDetail(movieId = movieId) }

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

    override suspend fun addFavoriteToCache(movie: MovieDetail) {
        movieDao.insertFavourite(movie.toFavoriteMovieEntity())
    }

    override suspend fun removeFavoriteFromCache(movieId: Int) {
        movieDao.deleteFavourite(movieId = movieId)
    }

    override suspend fun getFavouriteId(favoriteId: Int) : Boolean =
        movieDao.getFavoriteId(favoriteId = favoriteId)


}