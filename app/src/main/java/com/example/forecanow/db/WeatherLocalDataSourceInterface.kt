package com.example.forecanow.db


import com.example.forecanow.alarm.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

interface WeatherLocalDataSourceInterface {

    suspend fun addAlert(alert: WeatherAlert)

    suspend fun getStoredAlerts():Flow<List<WeatherAlert>>

    suspend fun deleteAlert(alert: WeatherAlert)


    suspend fun insertFavorite(favorite: FavoriteLocation)


    suspend fun deleteFavorite(favorite: FavoriteLocation)

    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    suspend fun getFavoriteById(id: Int): FavoriteLocation?

}