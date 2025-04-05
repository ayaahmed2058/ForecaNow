package com.example.forecanow.utils

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.example.forecanow.R
import java.util.Locale

object LocalizationHelper {
    private val arabicNumbers = listOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')

    @Composable
    fun isArabicLanguage(): Boolean {
        val configuration = LocalConfiguration.current
        return configuration.locales[0].language == "ar"
    }

    fun convertToArabicNumbers(number: String, context: Context): String {
        return if (isArabicLanguage(context)) {
            number.map { char ->
                if (char.isDigit()) arabicNumbers[char.toString().toInt()] else char
            }.joinToString("")
        } else {
            number
        }
    }

    fun isArabicLanguage(context: Context): Boolean {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            context.resources.configuration.locale
        }
        return locale.language == "ar"
    }

    @Composable
    fun getArabicWeatherDescription(description: String): String {
        return when (description.lowercase(Locale.getDefault())) {
            "clear sky" -> stringResource(R.string.clear_sky)
            "few clouds" -> stringResource(R.string.few_clouds)
            "scattered clouds" -> stringResource(R.string.scattered_clouds)
            "broken clouds" -> stringResource(R.string.broken_clouds)
            "shower rain" -> stringResource(R.string.shower_rain)
            "rain" -> stringResource(R.string.rain)
            "thunderstorm" -> stringResource(R.string.thunderstorm)
            "snow" -> stringResource(R.string.snow)
            "mist" -> stringResource(R.string.mist)
            else -> description
        }
    }

}