package com.example.cinesuggest.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class RatingRequest(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("movie_id")
    val movieId: Int,
    val rating: Int,
)

@Serializable
data class FavoriteRequest(
    @SerialName("user_id")
    val userId: Int,
    @SerialName("movie_id")
    val movieId: Int,
)