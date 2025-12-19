package com.example.cinesuggest.data.remote

import com.example.cinesuggest.data.remote.dto.FavoriteRequest
import com.example.cinesuggest.data.remote.dto.MovieDto
import com.example.cinesuggest.data.remote.dto.MovieDetailDto
import com.example.cinesuggest.data.remote.dto.MoviesResponseDto
import com.example.cinesuggest.data.remote.dto.RatingRequest
import com.example.cinesuggest.data.remote.dto.RecommendationResponseDto
import com.example.cinesuggest.data.remote.dto.UserProfile
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService{

    @GET("recommend/{user_id}")
    suspend fun getRecommendation(@Path("user_id") userId: Int) : RecommendationResponseDto

    @GET("movies")
    suspend fun getAllMovies() : MoviesResponseDto

    @POST("ratings")
    suspend fun rateMovie(@Body rating: RatingRequest) : Response<Unit>

    @POST("/favorites")
    suspend fun toggleFavorite(@Body body : FavoriteRequest) : Response<Unit>

    @GET("movies/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int , @Query("user_id") userId : Int): MovieDetailDto

    @GET("user/{user_id}/favorites")
    suspend fun getFavorites(@Path("user_id") userId: Int): List<MovieDto>

    @GET("user/{user_id}/profile")
    suspend fun getProfile(@Path("user_id") userId: Int): UserProfile
}