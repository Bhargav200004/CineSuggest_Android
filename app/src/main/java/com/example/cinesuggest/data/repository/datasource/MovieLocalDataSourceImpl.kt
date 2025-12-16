package com.example.cinesuggest.data.repository.datasource

import com.example.cinesuggest.data.local.dao.FavouriteMovieDao
import com.example.cinesuggest.data.mapper.toDomain
import com.example.cinesuggest.data.mapper.toEntity
import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.domain.repository.datasource.MovieLocalDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class MovieLocalDataSourceImpl @Inject constructor(
    private val dao : FavouriteMovieDao
) : MovieLocalDataSource {
    override fun getFavorites(): Flow<List<FavoriteMovie>> =
        dao.getAllFavorites().map {entities -> entities.map { favouriteMovieEntity -> favouriteMovieEntity.toDomain() } }

    override suspend fun isFavouriteCheck(movieId: Int): Boolean =
        dao.getFavoriteId(favoriteMovieId = movieId)

    override suspend fun addFavorite(movie: MovieDetail) =
        dao.insertFavourite(movie.toEntity())

    override suspend fun removeFavorite(movieId: Int) =
        dao.deleteFavourite(movieId = movieId)

}