package com.example.forecanow.data.network


import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class WeatherRemoteDataSourceImp(private val service: WeatherApiService ): WeatherRemoteDataSourceInterface {

    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<WeatherResponse> {

        val response = service.getCurrentWeather(lat, lon,units)
        val body = response.body()

        return if (response.isSuccessful && body != null) {
            flowOf(body)
        } else {
            flow { throw Exception("Error fetching weather: ${response.message()}") }
        }

    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<ForecastResponse> {
        try {
            val response = service.getHourlyForecast(lat, lon, units)
            println("API Key: ${service.getHourlyForecast(lat, lon, units, "YOUR_API_KEY")}")
            val body = response.body()
            println("Response Body: $body")

            return if (response.isSuccessful && body != null) {
                flowOf(body)
            } else {
                flow { throw Exception("Error: ${response.code()} - ${response.message()}") }
            }
        } catch (e: Exception) {
            println("Network Error: ${e.message}")
            throw e
        }
    }
}