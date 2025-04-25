package com.example.forecanow.data.network


import com.example.forecanow.data.pojo.ForecastResponse
import com.example.forecanow.data.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

class FakeWeatherRemoteDataSource: WeatherRemoteDataSourceInterface {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<WeatherResponse> {

        TODO("Not yet implemented")
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<ForecastResponse> {

        TODO("Not yet implemented")
    }
}