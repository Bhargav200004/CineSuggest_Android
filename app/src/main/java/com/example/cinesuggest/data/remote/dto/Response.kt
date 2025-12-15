package com.example.cinesuggest.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationResponse(
    val recommendations: List<Movie>
)

@Serializable
data class MoviesResponse(
    val movies: List<Movie>
)