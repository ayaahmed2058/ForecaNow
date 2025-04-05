package com.example.forecanow.data.network

import com.example.forecanow.pojo.Clouds
import com.example.forecanow.pojo.Coord
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.HourlyWeather
import com.example.forecanow.pojo.Main
import com.example.forecanow.pojo.MainInfo
import com.example.forecanow.pojo.Sys
import com.example.forecanow.pojo.Weather
import com.example.forecanow.pojo.WeatherDescription
import com.example.forecanow.pojo.WeatherResponse
import com.example.forecanow.pojo.Wind
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeWeatherRemoteDataSource: WeatherRemoteDataSourceInterface {
    override suspend fun getCurrentWeather(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<WeatherResponse> {

        val fakeWeatherResponse = WeatherResponse(
            coord = Coord(lon = lon, lat = lat),
            weather = listOf(
                Weather(id = 800, main = "Clear", description = "clear sky", icon = "01d")
            ),
            main = Main(
                temp = 25.0,
                feels_like = 24.5,
                temp_min = 22.0,
                temp_max = 28.0,
                pressure = 1012,
                humidity = 60
            ),
            wind = Wind(speed = 3.5, deg = 180),
            sys = Sys(country = "EG", sunrise = 1627790400L, sunset = 1627840800L),
            name = "Cairo",
            clouds = Clouds(all = 0)
        )
        return flowOf(fakeWeatherResponse)
    }

    override suspend fun getHourlyForecast(
        lat: Double,
        lon: Double,
        units: String
    ): Flow<ForecastResponse> {

        val fakeForecastResponse = ForecastResponse(
            list = listOf(
                HourlyWeather(
                    dt = 1627800000L,
                    main = MainInfo(temp = 24.0),
                    weather = listOf(
                        WeatherDescription(description = "clear", icon = "01d")
                    )
                ),
                HourlyWeather(
                    dt = 1627810800L,
                    main = MainInfo(temp = 26.0),
                    weather = listOf(
                        WeatherDescription(description = "sunny", icon = "02d")
                    )
                )
            )
        )
        return flowOf(fakeForecastResponse)

        //TODO("Not yet implemented")
    }
}