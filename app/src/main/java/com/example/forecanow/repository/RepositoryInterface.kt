package com.example.forecanow.repository


import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import com.example.forecanow.setting.AppSettings
import kotlinx.coroutines.flow.Flow

interface RepositoryInterface {

    suspend fun getWeather(lat: Double, lon: Double, units: String): Flow<WeatherResponse>

    suspend fun getHourlyForecast(lat: Double, lon: Double,  units: String): Flow<ForecastResponse>

    suspend fun getAllAlerts(): Flow<List<WeatherAlert>>
    suspend fun insertAlert(alert: WeatherAlert)
    suspend fun deleteAlert(alert: WeatherAlert)


    suspend fun insertFavorite(favorite: FavoriteLocation)
    suspend fun deleteFavorite(favorite: FavoriteLocation)
    fun getAllFavorites(): Flow<List<FavoriteLocation>>
    suspend fun getFavoriteById(id: Int): FavoriteLocation?

    suspend fun saveSettings(settings: AppSettings)
    suspend fun getSettings(): AppSettings?

}