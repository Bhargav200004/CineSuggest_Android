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
fun MovieDto.toDomain() : Movie{
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


// Movie Detail
// Dto -> domain
fun MovieDetailDto.toDomain() : MovieDetail {
    return MovieDetail(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue = this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview
    )
}

// domain -> uiState
fun MovieDetail.toUiState() : MovieDetailUiState {
    return MovieDetailUiState(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue =  this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
    )
}

// uiState -> domain
fun MovieDetailUiState.toDomain() : MovieDetail {
    return MovieDetail(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue =  this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
        userRating = this.userRating
    )
}



// Domain -> Entity
fun MovieDetail.toEntity() : FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genre = this.genres
    )
}

// Entity -> Domain
fun FavoriteMovieEntity.toDomain() : FavoriteMovie{
    return FavoriteMovie(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genres = this.genre
    )
}

