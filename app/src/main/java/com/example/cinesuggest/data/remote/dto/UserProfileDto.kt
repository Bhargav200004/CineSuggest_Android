package com.example.cinesuggest.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfile(
    val username: String,
    @SerialName("profile_image_url")
    val profileImageUrl: String,
    @SerialName("recent_ratings")
    val recentRatings: List<RatedMovie>
)