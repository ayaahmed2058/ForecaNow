package com.example.forecanow.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import android.media.RingtoneManager

class WeatherAlertReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertType = intent.getStringExtra("ALERT_TYPE")
        if (alertType == "Notification") {
            showNotification(context)
        } else {
            playAlarmSound(context)
        }
    }

    private fun showNotification(context: Context) {
        val channelId = "weather_alerts_channel"
        val channel = NotificationChannelCompat.Builder(channelId, NotificationManagerCompat.IMPORTANCE_HIGH)
            .setName("Weather Alerts")
            .setDescription("Weather alert notifications")
            .build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Weather Alert")
            .setContentText("Your weather alert is active!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        notificationManager.notify(1, notification)
    }

    private fun playAlarmSound(context: Context) {
        val ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        ringtone.play()
        Toast.makeText(context, "Weather alarm activated!", Toast.LENGTH_LONG).show()
    }
}
