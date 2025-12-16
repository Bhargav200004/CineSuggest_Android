package com.example.cinesuggest.data.mapper

import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import com.example.cinesuggest.domain.model.FavoriteMovie

fun FavoriteMovieEntity.toDomain(): FavoriteMovie {
    return FavoriteMovie(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genres = this.genre
    )
}