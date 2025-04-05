package com.example.forecanow.data.repository


import com.example.forecanow.data.db.FavoriteLocation
import com.example.forecanow.data.db.HourlyForecastEntity
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import com.example.forecanow.pojo.AppSettings
import kotlinx.coroutines.flow.Flow
import com.example.forecanow.data.db.WeatherAlert
import com.example.forecanow.data.db.WeatherEntity

interface RepositoryInterface {

    suspend fun getWeather(lat: Double, lon: Double, units: String): Flow<WeatherResponse>

    suspend fun getHourlyForecast(lat: Double, lon: Double,  units: String): Flow<ForecastResponse>

    suspend fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert): Long
    suspend fun deleteAlert(alert: WeatherAlert):Int
    suspend fun deleteAlertById(alertId: Int)
    suspend fun markAlertAsInactive(alertId: Int)

    suspend fun insertWeather(weather: WeatherEntity)
    suspend fun insertHourlyForecast(forecasts: List<HourlyForecastEntity>)
    suspend fun getWeather(city: String): WeatherEntity?

    suspend fun getHourlyForecast(city: String): List<HourlyForecastEntity>


    suspend fun insertFavorite(favorite: FavoriteLocation):Long
    suspend fun deleteFavorite(favorite: FavoriteLocation):Int
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    suspend fun getFavoriteById(id: Int): FavoriteLocation?

    suspend fun saveSettings(settings: AppSettings)
    suspend fun getSettings(): AppSettings?


}