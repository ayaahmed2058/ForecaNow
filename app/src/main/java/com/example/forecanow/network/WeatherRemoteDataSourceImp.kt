package com.example.forecanow.network


import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSourceImp(private val service: WeatherApiService): WeatherRemoteDataSourceInterface {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double
    ): Flow<WeatherResponse> {

        val response = service.getCurrentWeather(lat, lon)
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            flowOf(body)
        } else {
            flow { throw Exception("Error fetching weather: ${response.message()}") }
        }

    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double
    ): Flow<ForecastResponse> {
        val response = service.getHourlyForecast(lat, lon)
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            flowOf(body)
        } else {
            flow { throw Exception("Error fetching Hourly weather: ${response.message()}") }
        }
    }
}