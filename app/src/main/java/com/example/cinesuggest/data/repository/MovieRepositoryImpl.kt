package com.example.cinesuggest.data.repository

import com.example.cinesuggest.data.repository.datasource.MovieLocalDataSource
import com.example.cinesuggest.data.repository.datasource.MovieRemoteDataSource
import com.example.cinesuggest.domain.model.FavoriteMovie
import com.example.cinesuggest.domain.model.Movie
import com.example.cinesuggest.domain.model.MovieDetail
import com.example.cinesuggest.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepositoryImpl @Inject constructor(
    private val remoteDataSource : MovieRemoteDataSource,
    private val localDataSource: MovieLocalDataSource
): MovieRepository {
    override suspend fun getRecommendation(userId: Int): Result<List<Movie>> =
        remoteDataSource.getRecommendation(userId = userId)

    override suspend fun getAllMovies(): Result<List<Movie>> =
        remoteDataSource.getAllMovies()

    override suspend fun getMovieDetail(movieId: Int): Result<MovieDetail> =
        remoteDataSource.getMovieDetail(movieId = movieId)

    override suspend fun rateMovie(
        userId: Int,
        movieId: Int,
        rating: Int
    ): Result<Unit> =
        remoteDataSource.rateMovie(
            userId = userId,
            movieId = movieId,
            rating = rating
        )

    override suspend fun toggleFavorite(
        userId: Int,
        movieId: Int
    ): Result<Unit> =
        remoteDataSource.toggleFavorite(
            userId = userId,
            movieId = movieId
        )

    override fun getFavorites(): Flow<List<FavoriteMovie>> =
        localDataSource.getFavorites()

    override suspend fun addFavorite(movieDetail: MovieDetail) =
        localDataSource.addFavorite(movieDetail = movieDetail)

    override suspend fun removeFavorite(movieId: Int) =
        localDataSource.removeFavorite(movieId = movieId)

    override suspend fun isFavoriteCheck(favoriteMovieId: Int): Boolean =
        localDataSource.isFavoriteCheck(movieId = favoriteMovieId)

}