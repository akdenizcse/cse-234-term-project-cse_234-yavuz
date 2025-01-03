package com.example.weatherapplication.data.model.CurrentConditionsResponse

data class CurrentConditionsItem(
    val ApparentTemperature: ApparentTemperature,
    val Ceiling: Ceiling,
    val CloudCover: Double,
    val DewPoint: DewPoint,
    val EpochTime: Double,
    val HasPrecipitation: Boolean,
    val IndoorRelativeHumidity: Double,
    val IsDayTime: Boolean,
    val Link: String,
    val LocalObservationDateTime: String,
    val MobileLink: String,
    val ObstructionsToVisibility: String,
    val Past24HourTemperatureDeparture: Past24HourTemperatureDeparture,
    val Precip1hr: Precip1hr,
    val PrecipitationSummary: PrecipitationSummary,
    val PrecipitationType: Any,
    val Pressure: Pressure,
    val PressureTendency: PressureTendency,
    val RealFeelTemperature: RealFeelTemperature,
    val RealFeelTemperatureShade: RealFeelTemperatureShade,
    val RelativeHumidity: Double,
    val Temperature: Temperature,
    val TemperatureSummary: TemperatureSummary,
    val UVIndex: Double,
    val UVIndexText: String,
    val Visibility: Visibility,
    val WeatherIcon: Int,
    val WeatherText: String,
    val WetBulbGlobeTemperature: WetBulbGlobeTemperature,
    val WetBulbTemperature: WetBulbTemperature,
    val Wind: Wind,
    val WindChillTemperature: WindChillTemperature,
    val WindGust: WindGust
)