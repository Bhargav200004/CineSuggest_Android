package com.example.cinesuggest.data.mapper

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.remote.dto.MovieDetailDto
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.ui.screens.detail.MovieDetailUiState

fun MovieDetail.toMovieDetailUiState() : MovieDetailUiState {
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

fun MovieDetailDto.toMovieDetailDomain() : MovieDetail {
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

fun MovieDetailUiState.testFavourite() : MovieDetailDto {
    return MovieDetailDto(
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



fun MovieDetailDto.toFavoriteMovieEntity() : FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genre = this.genres
    )
}