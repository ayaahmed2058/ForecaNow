package com.example.forecanow.data.pojo


data class ForecastResponse(
    val list: List<HourlyWeather>
)

data class HourlyWeather(
    val dt: Long,
    val main: MainInfo,
    val weather: List<WeatherDescription>
)

data class MainInfo(
    val temp: Double
)

data class WeatherDescription(
    val description: String,
    val icon: String
)
