package com.example.weatherapplication.data.citiesDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class City(
    val cityName: String,
    val latitude: Float,
    val longitude: Float,
    val country: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int
)
