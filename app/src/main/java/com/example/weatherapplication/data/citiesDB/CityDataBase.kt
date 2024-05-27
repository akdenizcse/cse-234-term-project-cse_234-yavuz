package com.example.weatherapplication.data.citiesDB

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities =[City::class],
    version = 5
)
abstract class CityDataBase: RoomDatabase(){

    abstract fun getCityDao(): CityDao
}