package com.example.forecanow.pojo

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys,
    val name: String
)

data class Coord(
    val lon: Double,
    val lat: Double
)
