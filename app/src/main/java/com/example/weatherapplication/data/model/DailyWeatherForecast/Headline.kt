package com.example.weatherapplication.data.model.DailyWeatherForecast

data class Headline(
    val Category: String,
    val EffectiveDate: String,
    val EffectiveEpochDate: Int,
    val EndDate: String,
    val EndEpochDate: Int,
    val Link: String,
    val MobileLink: String,
    val Severity: Double,
    val Text: String
)