package com.example.cinesuggest.ui.screens.detail

sealed class MovieDetailUiEvent {
    data class OnFavoriteClick(val isFavorite: Boolean) : MovieDetailUiEvent()
    data class OnRatingChange(val rating : Int) : MovieDetailUiEvent()
}