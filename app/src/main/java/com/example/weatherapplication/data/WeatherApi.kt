package com.example.weatherapplication.data

import com.example.weatherapplication.data.model.HourlyData.HourlyData
import com.example.weatherapplication.data.model.LocationDataResponse.LocationData
import com.example.weatherapplication.data.model.SearchValues.SearcValues
import com.example.weatherapplication.data.model.CurrentConditionsResponse.CurrentConditions
import com.example.weatherapplication.data.model.DailyWeatherForecast.DailyWeatherForecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface WeatherApi {

    // get location id using latitude and longtitude
    @GET("/locations/v1/cities/geoposition/")
    suspend fun getLocationData(
        @Query("apikey") apikey: String,
        @Query("q") q:String,
        @Query("language") language: String="en-us",
        @Query("details") details: String="false",
        @Query("toplevel") toplevel: String="false"
    ): Response<LocationData>

    // autocomplete search to get the location key
    @GET("locations/v1/cities/autocomplete/")
    suspend fun getKeyByTextSearch(
        @Query("apikey") apikey: String,
        @Query("q") q: String,
        @Query("language") language: String="en-us"
    ): Response<List<SearcValues>>

    // get current weather condition with locationKey
    @GET("/currentconditions/v1/{locationKey}/")
    suspend fun getCurrentConditions(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String="en-us",
        @Query("details") details: String="true"
    ): Response<CurrentConditions>

    // get 12 hour forecast data
    @GET("/forecasts/v1/hourly/12hour/{locationKey}/")
    suspend fun getHourlyData(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String="en-us",
        @Query("details") details: String="true",
        @Query("metric") metric: String="true"
    ): Response<List<HourlyData>>

    // get daily weather forecast for 5 days with locationKey
    @GET("/forecasts/v1/daily/5day/{locationKey}/")
    suspend fun getDailyForecast(
        @Path("locationKey") locationKey: String,
        @Query("apikey") apikey: String,
        @Query("language") language: String="en-us",
        @Query("details") details: String="true",
        @Query("metric") metric: String="true"
    ): Response<DailyWeatherForecast>


}