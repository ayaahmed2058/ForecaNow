package com.example.forecanow.alarm

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecanow.LocationManager
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AlertViewModel(private val repository: AlertRepository) : ViewModel() {
    private val _alerts = MutableStateFlow<List<WeatherAlert>>(emptyList())
    val alerts: StateFlow<List<WeatherAlert>> = _alerts

    init {
        viewModelScope.launch {
            repository.getAllAlerts().collect { alertsList ->
                _alerts.value = alertsList
            }
        }
    }

    fun addAlert(type: String, startTime: Long, endTime: Long, alertType: String, context: Context) {
        val locationHelper = LocationManager(context, LocationServices.getFusedLocationProviderClient(context))

        locationHelper.getFreshLocation(
            onSuccess = { location ->
                viewModelScope.launch {
                    val newAlert = WeatherAlert(startTime = startTime, endTime = endTime, alertType = alertType)
                    repository.insertAlert(newAlert)
                    scheduleAlert(context, startTime, alertType, location.latitude, location.longitude)
                }
            },
            onFailure = { error ->
                Toast.makeText(context, "Failed to get location: $error", Toast.LENGTH_SHORT).show()
            }
        )
    }


    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlert(context: Context, timeInMillis: Long, alertType: String, lat: Double, lon: Double) {
        val intent = Intent(context, AlertReceiver::class.java).apply {
            putExtra("alertType", alertType)
            putExtra("latitude", lat)
            putExtra("longitude", lon)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    private fun cancelAlarm(context: Context, timeInMillis: Long) {
        val intent = Intent(context, AlertReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            timeInMillis.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun removeAlert(alert: WeatherAlert, context: Context) {
        viewModelScope.launch {
            repository.deleteAlert(alert)
            cancelAlarm(context, alert.startTime)
        }
    }
}

class AlarmViewModelFactory (private val repository: AlertRepository):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repository) as T
    }
}