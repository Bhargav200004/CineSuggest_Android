package com.example.cinesuggest.data.mapper

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.remote.dto.MovieDetail
import com.example.cinesuggest.ui.screens.detail.MovieDetailUiState

fun MovieDetail.toMovieDetailUiDataHolder() : MovieDetailUiState {
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

fun MovieDetailUiState.testFavourite() : MovieDetail {
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
    )
}

fun MovieDetail.toFavoriteMovieEntity() : FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genre = this.genres
    )
}