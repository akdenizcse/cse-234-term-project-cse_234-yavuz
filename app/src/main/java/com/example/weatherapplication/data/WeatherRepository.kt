package com.example.weatherapplication.data

import com.example.weatherapplication.data.model.LocationDataResponse.LocationData
import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.DailyWeatherForecast.DailyWeatherForecast
import com.example.weatherapplication.data.model.HourlyData.HourlyData
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    suspend fun getLocationData(apikey: String, q:String): Flow<Result<LocationData>>

    suspend fun getCurrentData(locationKey: String, apikey: String): Flow<Result<CurrentConditions>>

    suspend fun  getHourlyData(locationKey: String, apikey: String): Flow<Result<List<HourlyData>>>

    suspend fun getDailyForecast(locationKey: String,apikey: String): Flow<Result<DailyWeatherForecast>>
}