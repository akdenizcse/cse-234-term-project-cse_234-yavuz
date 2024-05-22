package com.example.weatherapplication.presentation

import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.DailyWeatherForecast.DailyWeatherForecast
import com.example.weatherapplication.data.model.HourlyData.HourlyData
import com.example.weatherapplication.data.model.LocationDataResponse.LocationData
import com.example.weatherapplication.getLatandLong

data class MainUiState(
    var locationData: LocationData?= null,
    var currentData: CurrentConditions?=null,
    var hourlyData: List<HourlyData>?=null,
    var lat_lon: String = "",
    var dailyData: DailyWeatherForecast?=null,
    val apiKey: String ="Gzjr81e3v0ALUMgqeGkwCEMaoUDLce78"
)

