package com.example.forecanow.utils

import com.example.forecanow.data.db.HourlyForecastEntity
import com.example.forecanow.data.db.WeatherEntity
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse

fun WeatherResponse.toEntity(): WeatherEntity {
    return WeatherEntity(
        cityName = name,
        country = sys.country,
        lat = coord.lat,
        lon = coord.lon,
        temp = main.temp,
        feelsLike = main.feels_like,
        tempMin = main.temp_min,
        tempMax = main.temp_max,
        pressure = main.pressure,
        humidity = main.humidity,
        windSpeed = wind.speed,
        windDeg = wind.deg,
        sunrise = sys.sunrise,
        sunset = sys.sunset,
        cloud = clouds.all,
        description = weather.firstOrNull()?.description ?: "",
        icon = weather.firstOrNull()?.icon ?: ""
    )
}

fun ForecastResponse.toEntities(city: String): List<HourlyForecastEntity> {
    return list.map {
        HourlyForecastEntity(
            dt = it.dt,
            cityName = city,
            temp = it.main.temp,
            description = it.weather.firstOrNull()?.description ?: "",
            icon = it.weather.firstOrNull()?.icon ?: ""
        )
    }
}
