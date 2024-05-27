package com.example.weatherapplication.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favouriteCities")
data class FavoriteCity(
    @PrimaryKey
    val cityName: String,
    val latitude:String,
    val longitude: String
)
