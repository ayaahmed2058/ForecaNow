package com.example.forecanow.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hourly_forecast")
data class HourlyForecastEntity(
    @PrimaryKey val dt: Long,
    val cityName: String,
    val temp: Double,
    val description: String,
    val icon: String
)
