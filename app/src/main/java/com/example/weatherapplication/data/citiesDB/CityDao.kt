package com.example.weatherapplication.data.citiesDB

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface CityDao {
    @Query("SELECT * FROM cities WHERE cityName LIKE :partialCityName COLLATE NOCASE")
    fun getCities(partialCityName: String): List<City>

}