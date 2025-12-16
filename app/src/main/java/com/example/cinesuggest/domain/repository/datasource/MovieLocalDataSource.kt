package com.example.cinesuggest.domain.repository.datasource

import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    fun getFavorites() : Flow<List<FavoriteMovie>>
    suspend fun addFavorite(movie: MovieDetail)
    suspend fun removeFavorite(movieId : Int)
    suspend fun isFavouriteCheck(movieId : Int) : Boolean
}