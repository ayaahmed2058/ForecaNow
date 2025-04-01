package com.example.forecanow.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val timestamp: Long = System.currentTimeMillis()
)