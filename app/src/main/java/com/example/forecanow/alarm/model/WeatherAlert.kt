package com.example.forecanow.alarm.model

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "weather_alerts")
data class WeatherAlert(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val startTime: Long,
    val endTime: Long,
    val alertType: String,
    val isActive: Boolean = true,
    val locationLat: Double = 0.0,
    val locationLon: Double = 0.0
)