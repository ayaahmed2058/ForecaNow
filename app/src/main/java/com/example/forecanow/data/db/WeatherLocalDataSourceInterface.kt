package com.example.forecanow.data.db


import com.example.forecanow.pojo.AppSettings
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSourceInterface {

    suspend fun addAlert(alert: WeatherAlert): Long

    suspend fun getStoredAlerts():Flow<List<WeatherAlert>>

    suspend fun deleteAlert(alert: WeatherAlert):Int

    suspend fun deleteAlertById(alertId: Int)
    suspend fun markAlertAsInactive(alertId: Int)

    suspend fun insertFavorite(favorite: FavoriteLocation):Long

    suspend fun insertWeather(weather: WeatherEntity)

    suspend fun insertHourlyForecast(forecasts: List<HourlyForecastEntity>)

    suspend fun getWeather(city: String): WeatherEntity?

    suspend fun getHourlyForecast(city: String): List<HourlyForecastEntity>



    suspend fun deleteFavorite(favorite: FavoriteLocation):Int

    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    suspend fun getFavoriteById(id: Int): FavoriteLocation?

    suspend fun saveSettings(settings: AppSettings)
    suspend fun getSettings(): AppSettings?


}