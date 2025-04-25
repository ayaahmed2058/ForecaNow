package com.example.forecanow.data.db

import android.util.Log
import com.example.forecanow.pojo.AppLanguage
import com.example.forecanow.pojo.AppSettings
import com.example.forecanow.pojo.LocationSource
import com.example.forecanow.pojo.TemperatureUnit
import com.example.forecanow.pojo.WindSpeedUnit
import kotlinx.coroutines.flow.Flow

class WeatherLocalDataSourceImp (private val dao: WeatherDao): WeatherLocalDataSourceInterface {
    override suspend fun addAlert(alert: WeatherAlert): Long {
        return dao.insertAlert(alert)
    }

    override suspend fun getStoredAlerts(): Flow<List<WeatherAlert>> {
        return dao.getAllAlerts()
    }

    override suspend fun deleteAlert(alert: WeatherAlert): Int {
        if(alert != null)
        return dao.deleteAlert(alert)

        return -1
    }

    override suspend fun insertFavorite(favorite: FavoriteLocation): Long {
        return dao.insertFavorite(favorite)
    }

    override suspend fun deleteFavorite(favorite: FavoriteLocation): Int {
        if(favorite != null)
        return dao.deleteFavorite(favorite)

        return -1
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return dao.getAllFavorites()
    }

    override suspend fun getFavoriteById(id: Int): FavoriteLocation? {
        return dao.getFavoriteById(id)
    }

    override suspend fun saveSettings(settings: AppSettings) {
        dao.saveSettings(
            SettingsEntity(
                temperatureUnit = settings.temperatureUnit.name,
                windSpeedUnit = settings.windSpeedUnit.name,
                language = settings.language.name,
                locationSource = settings.locationSource.name
            )
        )
    }

    override suspend fun getSettings(): AppSettings? {
        return dao.getSettings()?.let {
            try {
                AppSettings(
                    temperatureUnit = enumValueOf<TemperatureUnit>(it.temperatureUnit),
                    windSpeedUnit = enumValueOf<WindSpeedUnit>(it.windSpeedUnit),
                    language = enumValueOf<AppLanguage>(it.language),
                    locationSource = enumValueOf<LocationSource>(it.locationSource)
                )
            } catch (e: Exception) {
                Log.e("LocalDataSource", "Error parsing settings", e)
                null
            }
        }
    }
}