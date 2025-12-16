package com.example.cinesuggest.data.mapper

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.remote.dto.MovieDetailDto
import com.example.cinesuggest.data.remote.dto.MovieDto
import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.ui.screens.detail.MovieDetailUiState

// dto -> domain

// entity -> domain
// domain -> entity

// model -> uiState
// uiState -> domain


// Movie
// Dto -> domain
fun MovieDto.toDomain(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        genres = this.genre,
        posterUrl = this.posterUrl
    )
}

// domain -> uiState
// TODO : When i create Ui State for Movie


// uiState -> domain
// TODO : when i create Ui State for Movie

