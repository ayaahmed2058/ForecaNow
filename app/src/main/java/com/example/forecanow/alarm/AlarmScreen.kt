package com.example.forecanow.alarm

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


class AlarmScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAlertsScreen(this)
        }
    }
}

@Composable
fun WeatherAlertsScreen(context: Context) {
    var duration by remember { mutableStateOf(10) }
    var alertType by remember { mutableStateOf("Notification") }
    var isAlarmSet by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Weather Alerts", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Select Alert Duration (minutes):")
        Slider(value = duration.toFloat(), onValueChange = { duration = it.toInt() }, valueRange = 1f..60f)
        Text(text = "$duration minutes")

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Select Alert Type:")
        Row {
            listOf("Notification", "Alarm").forEach { type ->
                RadioButton(selected = alertType == type, onClick = { alertType = type })
                Text(text = type, modifier = Modifier.padding(end = 8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            setWeatherAlert(context, duration, alertType)
            isAlarmSet = true
        }) {
            Text(text = "Set Alert")
        }

        if (isAlarmSet) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
                cancelWeatherAlert(context)
                isAlarmSet = false
            }) {
                Text(text = "Cancel Alert")
            }
        }
    }
}

fun setWeatherAlert(context: Context, duration: Int, alertType: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WeatherAlertReceiver::class.java)
    intent.putExtra("ALERT_TYPE", alertType)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    val triggerTime = System.currentTimeMillis() + duration * 60 * 1000
    alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}

fun cancelWeatherAlert(context: Context) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val intent = Intent(context, WeatherAlertReceiver::class.java)
    val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    alarmManager.cancel(pendingIntent)
}
