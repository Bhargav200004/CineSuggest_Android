package com.example.cinesuggest.ui.screens.detail

data class MovieDetailUiState(
    val id: Int ,
    val title: String,
    val posterUrl: String,
    val releaseDate: String,
    val runtime: String,
    val genres: String,
    val revenue : Int,
    val originalLanguage: String,
    val overview: String,
    val userRating: Int  = 0,
    val isFavorite: Boolean = false
)
