package com.example.cinesuggest.domain.repository.datasource

import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.model.MovieDetail

interface MovieRemoteDataSource {
    suspend fun getRecommendation(userId : Int) : Result<List<Movie>>
    suspend fun getAllMovies() : Result<List<Movie>>
    suspend fun getMovieDetail(movieId : Int) : Result<MovieDetail>
    suspend fun rateMovie(userId : Int , movieId : Int , rating : Int ) : Result<Unit>
    suspend fun toggleFavorite(userId: Int, movieId: Int) : Result<Unit>
}