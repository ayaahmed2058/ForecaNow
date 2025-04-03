package com.example.forecanow.utils

import android.content.Context
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

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
}