package com.example.weatherapplication.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapplication.data.db.FavoriteCity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteCitiesDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(favoriteCity: FavoriteCity)

    @Query("SELECT * FROM favouriteCities")
    fun getAllFavCities(): Flow<List<FavoriteCity>>

    @Query("DELETE FROM favouriteCities WHERE cityName = :cityName")
    suspend fun deleteCity(cityName: String)
}
