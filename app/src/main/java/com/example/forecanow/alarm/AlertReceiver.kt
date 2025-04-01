package com.example.forecanow.alarm


import com.example.forecanow.R
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import android.os.Build
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.home.viewModel.HomeViewModel
import com.example.forecanow.model.Response
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return

        val alertType = intent.getStringExtra("alertType") ?: "Weather Alert"
        val lat = intent.getDoubleExtra("latitude", 0.0)
        val lon = intent.getDoubleExtra("longitude", 0.0)

        if (lat != 0.0 && lon != 0.0) {
            fetchWeatherAndShowNotification(context, alertType, lat, lon)
        } else {
            showNotification(context, alertType, "Location not available", "--°C")
        }
    }

    private fun fetchWeatherAndShowNotification(context: Context, alertType: String, lat: Double, lon: Double) {
        val repository =  RepositoryImp.getInstance(WeatherRemoteDataSourceImp(RetrofitHelper.api),
            WeatherLocalDataSourceInterfaceImp(WeatherDatabase.getDatabase(context).weatherDao()))
        val viewModel = HomeViewModel(repository)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                viewModel.getCurrentWeather(lat, lon)
                viewModel.weather.collect { response ->
                    if (response is Response.Success) {
                        val weather = response.data
                        val locationName = weather.name ?: "Unknown Location"
                        val temperature = "${weather.main.temp}°C"
                        showNotification(context, alertType, locationName, temperature)
                    }
                }
            } catch (e: Exception) {
                showNotification(context, alertType, "Error fetching location", "--°C")
            }
        }
    }


    private fun showNotification(context: Context, alertType: String, location: String, temperature: String) {
        val channelId = "weather_alerts"
        val channelName = "Weather Alerts"

        val soundUri = Uri.parse("android.resource://${context.packageName}/raw/alert_sound")
        //val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
                setSound(
                    soundUri, AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Weather Alert: $alertType")
            .setContentText("$location - Temperature: $temperature")
            .setSmallIcon(R.drawable.weather_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}


