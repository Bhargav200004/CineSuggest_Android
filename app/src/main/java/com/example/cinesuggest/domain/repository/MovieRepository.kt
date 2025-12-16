package com.example.cinesuggest.domain.repository

import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getRecommendation(userId : Int) : Result<List<Movie>>
    suspend fun getAllMovies() : Result<List<Movie>>
    suspend fun getMovieDetail(movieId : Int) : Result<MovieDetail>
    suspend fun rateMovie(userId : Int , movieId : Int , rating : Int ) : Result<Unit>
    suspend fun toggleFavorite(userId: Int, movieId: Int) : Result<Unit>
    fun getFavorites() : Flow<List<FavoriteMovie>>
    suspend fun isFavoriteCheck(favoriteMovieId : Int) : Boolean
    suspend fun addFavorite(movieDetail: MovieDetail)
    suspend fun removeFavorite(movieId : Int)
}