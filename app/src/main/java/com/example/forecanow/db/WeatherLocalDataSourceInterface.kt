package com.example.forecanow.db


import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.setting.AppSettings
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSourceInterface {

    suspend fun addAlert(alert: WeatherAlert)

    suspend fun getStoredAlerts():Flow<List<WeatherAlert>>

    suspend fun deleteAlert(alert: WeatherAlert)

    suspend fun deleteAlertById(alertId: Int)
    suspend fun markAlertAsInactive(alertId: Int)

    suspend fun insertFavorite(favorite: FavoriteLocation)


    suspend fun deleteFavorite(favorite: FavoriteLocation)

    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    suspend fun getFavoriteById(id: Int): FavoriteLocation?

    suspend fun saveSettings(settings: AppSettings)
    suspend fun getSettings(): AppSettings?

}