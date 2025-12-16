package com.example.cinesuggest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteMovieEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertFavorite(favoriteMovieEntity: FavoriteMovieEntity)

    @Query("DELETE FROM favorites WHERE id = :movieId")
    suspend fun deleteFavorite(movieId: Int)

    @Query("SELECT EXISTS (SELECT 1 FROM favorites WHERE  id == :favoriteMovieId)")
    suspend fun getFavoriteId(favoriteMovieId : Int) : Boolean

}