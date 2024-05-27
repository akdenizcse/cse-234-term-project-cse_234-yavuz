package com.example.weatherapplication.presentation

import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.DailyWeatherForecast.DailyWeatherForecast
import com.example.weatherapplication.data.model.HourlyData.HourlyData
import com.example.weatherapplication.data.model.LocationDataResponse.LocationData
import com.example.weatherapplication.data.model.SearchValues.SearchValues

data class MainUiState(
    var locationData: LocationData?= null,
    var currentData: CurrentConditions?=null,
    var hourlyData: List<HourlyData>?=null,
    var latitude: String = "",
    var longitude: String ="",
    var dailyData: DailyWeatherForecast?=null,
    val searchResult: List<SearchValues>?=null,
    var isLoading: Boolean = false,
    val apiKey: String ="T99aYclk3KjV2FVV3Q7SyAzeVGHKJ3tM"
)

