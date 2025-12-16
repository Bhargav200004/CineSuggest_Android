package com.example.cinesuggest.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RecommendationResponseDto(
    val recommendations: List<MovieDto>
)

@Serializable
data class MoviesResponseDto(
    val movies: List<MovieDto>
)