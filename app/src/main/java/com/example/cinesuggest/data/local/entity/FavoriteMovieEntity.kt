package com.example.cinesuggest.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cinesuggest.data.remote.dto.MovieDetail

@Entity(tableName = "favorites")
data class FavoriteMovieEntity(
    @PrimaryKey
    val id: Int,
    val title : String,
    val posterUrl: String?,
    val genre: String
)