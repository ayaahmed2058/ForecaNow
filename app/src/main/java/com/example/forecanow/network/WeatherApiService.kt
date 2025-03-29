package com.example.forecanow.network


import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface WeatherApiService {

    private val api : String
        get() = "a3365ae40ef680fba58631df13f8ac34"

    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String = api
    ): Response<WeatherResponse>


    @GET("forecast")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String = api,
        @Query("units") units: String = "metric"
    ): Response<ForecastResponse>
}