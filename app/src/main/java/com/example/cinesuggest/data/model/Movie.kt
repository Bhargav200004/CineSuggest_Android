package com.example.cinesuggest.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecommendationResponse(
    val recommendations: List<Movie>
)

@Serializable
data class MoviesResponse(
    val movies: List<Movie>
)

@Serializable
data class Movie(
    val id: Int,
    val title: String,
    val genre: String,
    @SerialName("poster_path")
    val posterUrl: String?,
)

@Serializable
data class MovieDetail(
    val id: Int,
    val title: String,
    @SerialName("poster_path")
    val posterUrl: String,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("runtime")
    val runtime: String,
    @SerialName("genre")
    val genres: String, // API provides a parsed list
    @SerialName("revenue")
    val revenue : Int,
    @SerialName("original_language")
    val originalLanguage: String,
    @SerialName("overview")
    val overview: String,
    @SerialName("user_rating")
    val userRating: Int?  = 1,
//    @SerialName("is_favorite")
//    val isFavorite: Boolean? = false
)

@Serializable
data class UserProfile(
    val username: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("recent_ratings")
    val recentRatings: List<RatedMovie>
)

@Serializable
data class RatedMovie(
    val id: Int,
    val title: String,
    @SerialName("poster_url")
    val posterUrl: String,
    val rating: Int // e.g., 4 out of 5
)