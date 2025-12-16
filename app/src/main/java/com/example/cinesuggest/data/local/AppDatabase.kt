package com.example.cinesuggest.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cinesuggest.data.local.dao.FavoriteMovieDao
import com.example.cinesuggest.data.local.entity.FavoriteMovieEntity

@Database(entities = [FavoriteMovieEntity::class] , version = 1 , exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao() : FavoriteMovieDao
}