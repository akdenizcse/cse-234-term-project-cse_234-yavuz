package com.example.weatherapplication

import android.app.Application
import androidx.room.Room
import com.example.weatherapplication.data.db.FavoriteCitiesDatabase

class MainApplication : Application() {

    companion object{
        lateinit var favoriteCitiesDatabase: FavoriteCitiesDatabase
    }

    override fun onCreate() {
        super.onCreate()
        favoriteCitiesDatabase = Room.databaseBuilder(
            applicationContext,
            FavoriteCitiesDatabase::class.java,
            "FavCity_DB"
        ).fallbackToDestructiveMigration()
            .build()
    }
}