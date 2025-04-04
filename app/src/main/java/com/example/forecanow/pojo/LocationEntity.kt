package com.example.forecanow.pojo


import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = false) val id: Int = 1,
    val name: String,
    val lat: Double,
    val lon: Double
)