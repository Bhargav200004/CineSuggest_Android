package com.example.cinesuggest.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cinesuggest.data.model.MovieDetail

@Entity(tableName = "favorites")
data class FavoriteMovieEntity(
    @PrimaryKey
    val id: Int,
    val title : String,
    val posterUrl: String?,
    val genre: String
)

fun MovieDetail.toFavoriteMovieEntity() : FavoriteMovieEntity {
    return FavoriteMovieEntity(
        id = this.id,
        title = this.title,
        posterUrl = this.posterUrl,
        genre = this.genres
    )
}