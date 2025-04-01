package com.example.forecanow.repository

import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.db.WeatherLocalDataSourceInterface
import com.example.forecanow.network.WeatherRemoteDataSourceInterface
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

class RepositoryImp private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSourceInterface,
    private val weatherLocalDataSource: WeatherLocalDataSourceInterface
) : RepositoryInterface{

    override suspend fun getWeather(lat: Double, lon: Double): Flow<WeatherResponse> {

        return weatherRemoteDataSource.getCurrentWeather(lat,lon)
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double
    ): Flow<ForecastResponse> {
        return weatherRemoteDataSource.getHourlyForecast(lat , lon)
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

    override suspend fun deleteFavorite(favorite: FavoriteLocation) {
        return weatherLocalDataSource.deleteFavorite(favorite)
    }

    override fun getAllFavorites(): Flow<List<FavoriteLocation>> {
        return weatherLocalDataSource.getAllFavorites()
    }

    override suspend fun getFavoriteById(id: Int): FavoriteLocation? {
        return weatherLocalDataSource.getFavoriteById(id)
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