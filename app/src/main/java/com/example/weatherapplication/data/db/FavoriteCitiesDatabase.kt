package com.example.weatherapplication.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [FavoriteCity::class],
    version = 4
)
abstract class FavoriteCitiesDatabase: RoomDatabase() {

    abstract fun getCityDao(): FavoriteCitiesDao
}