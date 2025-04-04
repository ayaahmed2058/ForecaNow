package com.example.forecanow.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.pojo.LocationEntity
import com.example.forecanow.setting.SettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_alerts")
    fun getAllAlerts(): Flow<List<WeatherAlert>>
    @Insert
    suspend fun insertAlert(alert: WeatherAlert)
    @Delete
    suspend fun deleteAlert(alert: WeatherAlert)

    @Query("DELETE FROM weather_alerts WHERE id = :alertId")
    suspend fun deleteAlertById(alertId: Int)

    @Query("UPDATE weather_alerts SET isActive = 0 WHERE id = :alertId")
    suspend fun markAlertAsInactive(alertId: Int)

    @Insert
    suspend fun insertFavorite(favorite: FavoriteLocation)

    @Delete
    suspend fun deleteFavorite(favorite: FavoriteLocation)

    @Query("SELECT * FROM favorite_locations ORDER BY timestamp DESC")
    fun getAllFavorites(): Flow<List<FavoriteLocation>>

    @Query("SELECT * FROM favorite_locations WHERE id = :id")
    suspend fun getFavoriteById(id: Int): FavoriteLocation?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSettings(settings: SettingsEntity)

    @Query("SELECT * FROM settings LIMIT 1")
    suspend fun getSettings(): SettingsEntity?


}