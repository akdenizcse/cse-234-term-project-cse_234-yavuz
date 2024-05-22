package com.example.weatherapplication.data.model.LocationDataResponse

data class TimeZone(
    val Code: String,
    val GmtOffset: Int,
    val IsDaylightSaving: Boolean,
    val Name: String,
    val NextOffsetChange: Any
)