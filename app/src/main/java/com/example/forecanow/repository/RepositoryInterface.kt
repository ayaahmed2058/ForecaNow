package com.example.forecanow.repository

import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherResponse>

    suspend fun getHourlyForecast(lat: Double, lon: Double): Flow<ForecastResponse>
}