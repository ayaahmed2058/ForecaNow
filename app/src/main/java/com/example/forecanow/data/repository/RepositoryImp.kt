package com.example.forecanow.data.repository

import android.util.Log
import com.example.forecanow.data.db.FavoriteLocation
import com.example.forecanow.data.db.HourlyForecastEntity
import com.example.forecanow.data.db.WeatherAlert
import com.example.forecanow.data.db.WeatherEntity
import com.example.forecanow.data.db.WeatherLocalDataSourceInterface
import com.example.forecanow.data.network.WeatherRemoteDataSourceInterface
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import com.example.forecanow.pojo.AppSettings
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

    override suspend fun insertAlert(alert: WeatherAlert): Long {
        return weatherLocalDataSource.addAlert(alert)
    }

    override suspend fun deleteAlert(alert: WeatherAlert):Int {
        return weatherLocalDataSource.deleteAlert(alert)
    }

    override suspend fun insertFavorite(favorite: FavoriteLocation):Long {
        return weatherLocalDataSource.insertFavorite(favorite)
    }

    override suspend fun deleteAlertById(alertId: Int){
        return weatherLocalDataSource.deleteAlertById(alertId)
    }

    override suspend fun markAlertAsInactive(alertId: Int){
        return weatherLocalDataSource.markAlertAsInactive(alertId)
    }

    override suspend fun insertWeather(weather: WeatherEntity) {
        return weatherLocalDataSource.insertWeather(weather)
    }

    override suspend fun insertHourlyForecast(forecasts: List<HourlyForecastEntity>) {
        return weatherLocalDataSource.insertHourlyForecast(forecasts)
    }

    override suspend fun getWeather(city: String): WeatherEntity? {
        return weatherLocalDataSource.getWeather(city)
    }

    override suspend fun getHourlyForecast(city: String): List<HourlyForecastEntity> {
        return weatherLocalDataSource.getHourlyForecast(city)
    }

    override suspend fun deleteFavorite(favorite: FavoriteLocation) :Int{
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