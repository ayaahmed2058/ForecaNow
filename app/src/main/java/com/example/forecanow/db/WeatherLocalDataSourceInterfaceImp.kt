package com.example.forecanow.db

import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.setting.AppLanguage
import com.example.forecanow.setting.AppSettings
import com.example.forecanow.setting.LocationSource
import com.example.forecanow.setting.SettingsEntity
import com.example.forecanow.setting.TemperatureUnit
import com.example.forecanow.setting.WindSpeedUnit
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
        val entity = dao.getSettings() ?: return null
        return AppSettings(
            temperatureUnit = TemperatureUnit.valueOf(entity.temperatureUnit),
            windSpeedUnit = WindSpeedUnit.valueOf(entity.windSpeedUnit),
            language = AppLanguage.valueOf(entity.language),
            locationSource = LocationSource.valueOf(entity.locationSource)
        )
    }
}