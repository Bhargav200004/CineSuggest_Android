package com.example.cinesuggest.data.remote

import com.example.cinesuggest.data.model.*
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService{

    @GET("recommend/{user_id}")
    suspend fun getRecommendation(@Path("user_id") userId: Int) : RecommendationResponse

    @GET("movies")
    suspend fun getAllMovies() : List<Movie>

    @POST("rating")
    suspend fun rateMovie(@Body rating: RatingRequest) : Response<Unit>

    @POST
    suspend fun toggleFavorite(@Body body : FavoriteRequest) : Response<Unit>

    @GET("movies/{movie_id}")
    suspend fun getMovieDetail(@Path("movie_id") movieId: Int): MovieDetail

    @GET("user/{user_id}/favorites")
    suspend fun getFavorites(@Path("user_id") userId: Int): List<Movie>

    @GET("user/{user_id}/profile")
    suspend fun getProfile(@Path("user_id") userId: Int): UserProfile
}