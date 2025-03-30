package com.example.forecanow.alarm

import kotlinx.coroutines.flow.Flow

class AlertRepository(private val dao: AlertDao) {
    fun getAllAlerts(): Flow<List<WeatherAlert>> = dao.getAllAlerts()
    suspend fun insertAlert(alert: WeatherAlert) = dao.insertAlert(alert)
    suspend fun deleteAlert(alert: WeatherAlert) = dao.deleteAlert(alert)
}