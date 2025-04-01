package com.example.forecanow.db

import com.example.forecanow.alarm.model.WeatherAlert
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceInterfaceImp (private val dao: WeatherDao): WeatherLocalDataSourceInterface {
    override suspend fun addAlert(alert: WeatherAlert) {
        return dao.insertAlert(alert)
    }

    override suspend fun getStoredAlerts(): Flow<List<WeatherAlert>> {
        return dao.getAllAlerts()
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
        return dao.deleteAlert(alert)
    }

    override suspend fun insertFavorite(favorite: FavoriteLocation) {
        return dao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteLocation) {
        return dao.deleteFavorite(favorite)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavorites()
    }

    override suspend fun getFavoriteById(id: Int): FavoriteLocation? {
        return dao.getFavoriteById(id)
    }
}