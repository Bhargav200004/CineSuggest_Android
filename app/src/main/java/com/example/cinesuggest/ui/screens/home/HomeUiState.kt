package com.example.cinesuggest.ui.screens.home

import com.example.cinesuggest.domain.model.Movie

data class HomeUiState(
    val movies : List<Movie> = emptyList()
)