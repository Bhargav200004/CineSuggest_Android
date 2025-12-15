package com.example.cinesuggest.ui.screens.detail

sealed class MovieDetailUiEvent {
    data class OnFavouriteClick(val isFavorite: Boolean) : MovieDetailUiEvent()
    data class OnRatingChange(val rating : Int) : MovieDetailUiEvent()
}