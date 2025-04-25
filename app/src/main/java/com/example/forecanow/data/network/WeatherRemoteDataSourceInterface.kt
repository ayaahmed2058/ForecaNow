package com.example.forecanow.data.network


import com.example.forecanow.data.pojo.ForecastResponse
import com.example.forecanow.data.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRemoteDataSourceInterface {

    suspend fun getCurrentWeather(lat: Double, lon: Double, units: String)
            : Flow<WeatherResponse>

    suspend fun getHourlyForecast (lat: Double, lon: Double, units: String)
            : Flow<ForecastResponse>

}