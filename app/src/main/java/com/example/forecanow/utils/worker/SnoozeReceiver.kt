package com.example.forecanow.utils.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class SnoozeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alertType = intent.getStringExtra("alert_type") ?: "Alert"
        val location = intent.getStringExtra("location") ?: "Unknown"

        Toast.makeText(context, "Snoozed for 10 minutes", Toast.LENGTH_SHORT).show()

        val workRequest = OneTimeWorkRequestBuilder<WeatherAlertWorker>()
            .setInputData(workDataOf(
                "alert_type" to alertType,
                "latitude" to 0.0,
                "longitude" to 0.0
            ))
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueue(workRequest)
    }
}
