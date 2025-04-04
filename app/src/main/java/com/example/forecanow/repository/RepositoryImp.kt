package com.example.forecanow.repository

import android.util.Log
import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.db.WeatherLocalDataSourceInterface
import com.example.forecanow.network.WeatherRemoteDataSourceInterface
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import com.example.forecanow.setting.AppSettings
import kotlinx.coroutines.flow.Flow

class RepositoryImp private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSourceInterface,
    private val weatherLocalDataSource: WeatherLocalDataSourceInterface
) : RepositoryInterface{

    override suspend fun getWeather(lat: Double, lon: Double , units: String): Flow<WeatherResponse> {

        return weatherRemoteDataSource.getCurrentWeather(lat,lon,units)
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<ForecastResponse> {
        return weatherRemoteDataSource.getHourlyForecast(lat , lon,units)
    }

    override suspend fun getAllAlerts(): Flow<List<WeatherAlert>> {
        return weatherLocalDataSource.getStoredAlerts()
    }

    override suspend fun insertAlert(alert: WeatherAlert) {
        return weatherLocalDataSource.addAlert(alert)
    }

    override suspend fun deleteAlert(alert: WeatherAlert) {
        return weatherLocalDataSource.deleteAlert(alert)
    }

    override suspend fun insertFavorite(favorite: FavoriteLocation) {
        return weatherLocalDataSource.insertFavorite(favorite)
    }

    override suspend fun deleteAlertById(alertId: Int){
        return weatherLocalDataSource.deleteAlertById(alertId)
    }

    override suspend fun markAlertAsInactive(alertId: Int){
        return weatherLocalDataSource.markAlertAsInactive(alertId)
    }

    override suspend fun deleteFavorite(favorite: FavoriteLocation) {
        return weatherLocalDataSource.deleteFavorite(favorite)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return weatherLocalDataSource.getAllFavorites()
    }

    override suspend fun getFavoriteById(id: Int): FavoriteLocation? {
        return weatherLocalDataSource.getFavoriteById(id)
    }

    override suspend fun saveSettings(settings: AppSettings) {
        try {
            weatherLocalDataSource.saveSettings(settings)
        } catch (e: Exception) {
            Log.e("Repository", "Error saving settings", e)
            throw e
        }
    }

    override suspend fun getSettings(): AppSettings? {
        return try {
            weatherLocalDataSource.getSettings()
        } catch (e: Exception) {
            Log.e("Repository", "Error loading settings", e)
            null
        }
    }


    companion object{
        @Volatile
        private var INSTANCE: RepositoryImp? = null

        fun getInstance(weatherRemoteDataSource: WeatherRemoteDataSourceInterface,
                        weatherLocalDataSource: WeatherLocalDataSourceInterface
        ): RepositoryImp {
            return INSTANCE ?: synchronized(this){
                val temp = RepositoryImp(weatherRemoteDataSource , weatherLocalDataSource)
                INSTANCE = temp
                temp
            }
        }
    }
}