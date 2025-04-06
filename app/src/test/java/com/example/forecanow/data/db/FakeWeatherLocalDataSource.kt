package com.example.forecanow.data.db

import com.example.forecanow.pojo.AppSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherLocalDataSource(private val alerts: MutableList<WeatherAlert> = mutableListOf(),
                                 private val favorites: MutableList<FavoriteLocation> = mutableListOf()

) : WeatherLocalDataSourceInterface {

    override suspend fun addAlert(alert: WeatherAlert): Long {
        alerts.add(alert)
        if(alerts.contains(alert))
            return  1
        else
            return 0
    }

    override suspend fun deleteAlert(alert: WeatherAlert) : Int{
        val isRemoved = alerts.remove(alert)
        if(isRemoved)
            return 1
        else
            return 0
    }

    override suspend fun insertFavorite(favorite: FavoriteLocation): Long {
        favorites.add(favorite)
        if(favorites.contains(favorite))
            return 1
        else
            return 0
    }


    override suspend fun deleteFavorite(favorite: FavoriteLocation): Int {
        val isRemoved = favorites.remove(favorite)
        if(isRemoved)
            return 1
        else
            return 0
    }


    override suspend fun getStoredAlerts(): Flow<List<WeatherAlert>> {
        return flowOf(alerts)
    }


    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        TODO("Not yet implemented")
    }

    override suspend fun getFavoriteById(id: Int): FavoriteLocation? {
        TODO("Not yet implemented")
    }

    override suspend fun saveSettings(settings: AppSettings) {
        TODO("Not yet implemented")
    }

    override suspend fun getSettings(): AppSettings? {
        TODO("Not yet implemented")
    }
}