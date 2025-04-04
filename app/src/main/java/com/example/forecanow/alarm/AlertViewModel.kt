package com.example.forecanow.alarm


import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.forecanow.utils.LocationManager
import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.repository.RepositoryImp
import com.google.android.gms.location.LocationServices


class AlertViewModel(private val repository: RepositoryImp) : ViewModel() {
    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts

    init {
        getAllAlerts()
        startPeriodicAlertCheck()
    }

    private fun getAllAlerts() {
        viewModelScope.launch {
            repository.getAllAlerts().collect { alertsList ->
                _alerts.value = alertsList
            }
        }
    }

    fun addAlert(startTime: Long, endTime: Long, alertType: String, context: Context) {
        val locationHelper = LocationManager(context, LocationServices.getFusedLocationProviderClient(context))

        locationHelper.getFreshLocation(
            onSuccess = { location ->
                viewModelScope.launch {
                    val newAlert = WeatherAlert(
                        startTime = startTime,
                        endTime = endTime,
                        alertType = alertType,
                        locationLat = location.latitude,
                        locationLon = location.longitude
                    )
                    repository.insertAlert(newAlert)
                    scheduleAlert(context, newAlert)
                }
            },
            onFailure = { error ->
                Toast.makeText(context, "Failed to get location: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun scheduleAlert(context: Context, alert: WeatherAlert) {
        val currentTime = System.currentTimeMillis()

        if (alert.startTime <= currentTime) {
            Toast.makeText(context, "Alert time must be in the future", Toast.LENGTH_SHORT).show()
            return
        }

        val inputData = workDataOf(
            "alert_id" to alert.id,
            "alert_type" to alert.alertType,
            "latitude" to alert.locationLat,
            "longitude" to alert.locationLon
        )

        val request = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .setInitialDelay(alert.startTime - currentTime, TimeUnit.MILLISECONDS)
            .addTag("weather_alert_${alert.id}")
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun scheduleOneTimeAlert(context: Context, alert: WeatherAlert) {
        val delay = alert.startTime - System.currentTimeMillis()

        val inputData = workDataOf(
            "alert_id" to alert.id,
            "alert_type" to alert.alertType,
            "latitude" to alert.locationLat,
            "longitude" to alert.locationLon
        )

        val request = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun triggerAlertImmediately(context: Context, alert: WeatherAlert) {
        val inputData = workDataOf(
            "alert_id" to alert.id,
            "alert_type" to alert.alertType,
            "latitude" to alert.locationLat,
            "longitude" to alert.locationLon
        )

        val request = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(inputData)
            .build()

        WorkManager.getInstance(context).enqueue(request)
    }

    private fun startPeriodicAlertCheck() {
        viewModelScope.launch {
            while (true) {
                delay(15 * 60 * 1000)
                checkActiveAlerts()
            }
        }
    }

    private fun checkActiveAlerts() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val activeAlerts = _alerts.value.filter {
                it.isActive && it.startTime <= currentTime && it.endTime >= currentTime
            }

            activeAlerts.forEach { alert ->
            }
        }
    }

    fun removeAlert(alert: WeatherAlert, context: Context) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
        }
    }
}

class AlarmViewModelFactory (private val repository: RepositoryImp):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repository) as T
    }
}