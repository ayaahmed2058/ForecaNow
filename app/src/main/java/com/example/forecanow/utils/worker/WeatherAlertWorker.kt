package com.example.forecanow.utils.worker

import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.util.Log
import com.example.forecanow.R
import com.example.forecanow.home.viewModel.HomeViewModel
import com.example.forecanow.data.Response
import com.example.forecanow.data.db.WeatherDatabase
import com.example.forecanow.data.db.WeatherLocalDataSourceImp
import com.example.forecanow.data.network.RetrofitHelper
import com.example.forecanow.data.network.WeatherRemoteDataSourceImp
import com.example.forecanow.data.repository.RepositoryImp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            createNotificationChannel(applicationContext)

            val alertId = inputData.getInt("alert_id", -1)
            val alertType = inputData.getString("alert_type") ?: "Notification"
            val lat = inputData.getDouble("latitude", 0.0)
            val lon = inputData.getDouble("longitude", 0.0)

            if (lat == 0.0 || lon == 0.0) {
                Log.e("WeatherAlertWorker", "Invalid location data (lat: $lat, lon: $lon)")
                showNotification(applicationContext, alertType, "Location not available", "--°C")
                return Result.failure()
            }

            fetchWeatherAndShowNotification(applicationContext, alertType, lat, lon)
            Result.success()
        } catch (e: Exception) {
            Log.e("WeatherAlertWorker", "Error in doWork", e)
            Result.retry()
        }
    }



    private fun fetchWeatherAndShowNotification(context: Context, alertType: String, lat: Double, lon: Double) {
        val repository =  RepositoryImp.getInstance(WeatherRemoteDataSourceImp(RetrofitHelper.api),
            WeatherLocalDataSourceImp(WeatherDatabase.getDatabase(context).weatherDao()))
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

        val isAlarm = alertType.equals("Alarm", ignoreCase = true) || alertType.equals("Alert", ignoreCase = true)

        val soundUri = if (isAlarm) {
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        } else {
            Uri.parse("android.resource://${context.packageName}/raw/alarm_sound")
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = if (isAlarm) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Weather alert notifications"
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
                enableVibration(true)
                if (isAlarm) {
                    setBypassDnd(true)
                }
            }
            notificationManager.createNotificationChannel(channel)
        }


        val snoozeIntent = Intent(context, SnoozeReceiver::class.java).apply {
            putExtra("alert_type", alertType)
            putExtra("location", location)
        }

        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            1,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val dismissIntent = Intent(context, DismissReceiver::class.java).apply {
            putExtra("notification_id", System.currentTimeMillis().toInt())
        }

        val dismissPendingIntent = PendingIntent.getBroadcast(
            context,
            2,
            dismissIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Weather Alert: $alertType")
            .setContentText("$location - Temperature: $temperature")
            .setSmallIcon(R.drawable.weather_icon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSound(soundUri)
            .setAutoCancel(true)

        if (isAlarm) {
            notificationBuilder.addAction(R.drawable.snooze, "Snooze", snoozePendingIntent)
            notificationBuilder.addAction(R.drawable.cancel_1, "Dismiss", dismissPendingIntent)
        }

        val notification = notificationBuilder.build()

        val notificationId = dismissIntent.getIntExtra("notification_id", 0)
        notificationManager.notify(notificationId, notification)
    }



    private fun createNotificationChannel(context: Context) {
        val soundUri =
            Uri.parse("android.resource://" + context.packageName + "/" + R.raw.backgroundmusic)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "weather_alerts",
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Weather alert notifications"
                enableVibration(true)
                setSound(
                    soundUri,
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .build()
                )
            }
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
