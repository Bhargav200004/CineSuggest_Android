package com.example.cinesuggest.domain.repository

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.remote.dto.MovieDetailDto
import com.example.cinesuggest.data.remote.dto.MoviesResponseDto
import com.example.cinesuggest.data.remote.dto.RecommendationResponseDto
import com.example.cinesuggest.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface MovieRepository {
    suspend fun getRecommendation(userId : Int) : Result<RecommendationResponseDto>
    suspend fun getAllMovies() : Result<MoviesResponseDto>
    suspend fun getMovieDetail(movieId : Int) : Result<MovieDetail>
    suspend fun rateMovie(userId : Int , movieId : Int , rating : Int ) : Result<Response<Unit>>
    suspend fun toggleFavorite(userId: Int, movieId: Int) : Result<Response<Unit>>
    fun getFavoritesCache() : Flow<List<FavoriteMovieEntity>>
    suspend fun getFavouriteId(favoriteId : Int) : Boolean
    suspend fun addFavoriteToCache(movie: MovieDetailDto)
    suspend fun removeFavoriteFromCache(movieId : Int)
}