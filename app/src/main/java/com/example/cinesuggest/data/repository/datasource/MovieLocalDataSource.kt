package com.example.cinesuggest.data.repository.datasource

import com.example.cinesuggest.data.local.dao.FavoriteMovieDao
import com.example.cinesuggest.data.mapper.toDomain
import com.example.cinesuggest.data.mapper.toEntity
import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.MovieDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface MovieLocalDataSource {
    fun getFavorites() : Flow<List<FavoriteMovie>>
    suspend fun addFavorite(movieDetail: MovieDetail)
    suspend fun removeFavorite(movieId : Int)
    suspend fun isFavoriteCheck(movieId : Int) : Boolean
}

class MovieLocalDataSourceImpl @Inject constructor(
    private val dao : FavoriteMovieDao
) : MovieLocalDataSource {
    override fun getFavorites(): Flow<List<FavoriteMovie>> =
        dao.getAllFavorites().map {entities -> entities.map { FavoriteMovieEntity -> FavoriteMovieEntity.toDomain() } }

    override suspend fun isFavoriteCheck(movieId: Int): Boolean =
        dao.getFavoriteId(favoriteMovieId = movieId)

    override suspend fun addFavorite(movieDetail: MovieDetail) =
        dao.insertFavorite(movieDetail.toEntity())

    override suspend fun removeFavorite(movieId: Int) =
        dao.deleteFavorite(movieId = movieId)

}