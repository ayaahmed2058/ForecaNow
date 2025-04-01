package com.example.forecanow.setting

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val temperatureUnit: String,
    val windSpeedUnit: String,
    val language: String,
    val locationSource: String
)