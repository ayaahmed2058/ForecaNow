package com.example.forecanow.repository

import com.example.forecanow.network.WeatherRemoteDataSourceInterface
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import kotlinx.coroutines.flow.Flow

class RepositoryImp private constructor(
    private val weatherRemoteDataSource: WeatherRemoteDataSourceInterface,
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


    companion object{
        @Volatile
        private var INSTANCE: RepositoryImp? = null

        fun getInstance(weatherRemoteDataSource: WeatherRemoteDataSourceInterface
        ): RepositoryImp {
            return INSTANCE ?: synchronized(this){
                val temp = RepositoryImp(weatherRemoteDataSource)
                INSTANCE = temp
                temp
            }
        }
    }

}