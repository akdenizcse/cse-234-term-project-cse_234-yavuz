package com.example.weatherapplication

import android.app.Application
import androidx.room.Room
import com.example.weatherapplication.data.citiesDB.CityDataBase
import com.example.weatherapplication.data.db.FavoriteCitiesDatabase

class MainApplication : Application() {

    companion object{
        lateinit var favoriteCitiesDatabase: FavoriteCitiesDatabase
        lateinit var cityDataBase: CityDataBase
    }

    override fun onCreate() {
        super.onCreate()
        favoriteCitiesDatabase = Room.databaseBuilder(
            applicationContext,
            FavoriteCitiesDatabase::class.java,
            "FavCity_DB"
        ).fallbackToDestructiveMigration()
            .build()

        cityDataBase = Room.databaseBuilder(
            applicationContext,
            CityDataBase::class.java,
            "City_DB"
        ).fallbackToDestructiveMigration()
            .createFromAsset("database/cities.db")
            .build()
    }
}