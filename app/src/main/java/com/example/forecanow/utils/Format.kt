package com.example.forecanow.utils

import android.content.Context
import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Format {

    @Composable
    fun formatTime(timestamp: Long, context: Context): String {
        val time = if (LocalizationHelper.isArabicLanguage()) {
            SimpleDateFormat("hh:mm a", Locale("ar")).format(Date(timestamp * 1000))
        } else {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp * 1000))
        }
        return LocalizationHelper.convertToArabicNumbers(time,context)
    }

    @Composable
    fun formatDate(timestamp: Long, context: Context): String {
        val date = if (LocalizationHelper.isArabicLanguage()) {
            SimpleDateFormat("EEE, MMM dd", Locale("ar")).format(Date(timestamp * 1000))
        } else {
            SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(Date(timestamp * 1000))
        }
        return LocalizationHelper.convertToArabicNumbers(date,context)
    }
}