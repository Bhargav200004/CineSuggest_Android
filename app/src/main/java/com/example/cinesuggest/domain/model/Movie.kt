package com.example.cinesuggest.domain.model


data class Movie(
    val id: Int,
    val title: String,
    val genres: String,
    val posterUrl: String?,
)

data class MovieDetail(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val releaseDate: String,
    val runtime: String,
    val genres: String,
    val revenue : Int,
    val originalLanguage: String,
    val overview: String,
    val userRating: Int?  = 1,
)

data class FavoriteMovie(
    val id: Int,
    val title : String,
    val posterUrl: String?,
    val genres: String
)

data class RatedMovie(
    val id: Int,
    val title: String,
    val posterUrl: String,
    val rating: Int
)