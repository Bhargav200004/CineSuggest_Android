package com.example.cinesuggest.data.mapper

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.data.remote.dto.MovieDetailDto
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.ui.screens.detail.MovieDetailUiState

fun MovieDetailDto.toDomain(): MovieDetail {
    return MovieDetail(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue = this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
        userRating = this.userRating
    )
}

fun MovieDetail.toUiState(): MovieDetailUiState {
    return MovieDetailUiState(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue = this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
        userRating = this.userRating ?: 0
    )
}

fun MovieDetailUiState.toDomain(): MovieDetail {
    return MovieDetail(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        releaseDate = this.releaseDate,
        runtime = this.runtime,
        genres = this.genres,
        revenue = this.revenue,
        originalLanguage = this.originalLanguage,
        overview = this.overview,
        userRating = this.userRating
    )
}


fun MovieDetail.toEntity(): FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genre = this.genres
    )
}