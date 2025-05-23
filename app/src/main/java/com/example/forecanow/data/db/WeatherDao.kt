package com.example.forecanow.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<WeatherAlert>>
    @Insert
    suspend fun insertAlert(alert: WeatherAlert): Long
    @Delete
    suspend fun deleteAlert(alert: WeatherAlert):Int

    @Insert
    suspend fun insertFavorite(favorite: FavoriteLocation): Long

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteLocation):Int

    @Query("SELECT * FROM favorite_locations ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE id = :id")
    suspend fun getFavoriteById(id: Int): FavoriteLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)

    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun getSettings(): SettingsEntity?
}