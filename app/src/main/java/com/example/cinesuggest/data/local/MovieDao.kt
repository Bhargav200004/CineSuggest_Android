package com.example.cinesuggest.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favoriteMovieEntity: FavoriteMovieEntity)

    @Query("DELETE FROM favorites WHERE id = :movieId")
    suspend fun delete(movieId: Int)
}