package com.example.forecanow.utils

import com.example.forecanow.R

class MapImage {
    companion object {
        fun getWeatherIconRes(iconCode: String): Int {
            return when (iconCode) {
                "01d" -> R.drawable._01d4x
                "01n" -> R.drawable._01n4x
                "02d" -> R.drawable._02d4x
                "02n" -> R.drawable._02n4x
                "03d" -> R.drawable._03d4x
                "03n" -> R.drawable._03n4x
                "04d" -> R.drawable._04d4x
                "04n" -> R.drawable._04n4x
                "09d" -> R.drawable._09d4x
                "09n" -> R.drawable._09n4x
                "10d" -> R.drawable._10d4x
                "10n" -> R.drawable._10n4x
                "11d" -> R.drawable._11d4x
                "11n" -> R.drawable._11n4x
                "13d" -> R.drawable._13d4x
                "13n" -> R.drawable._13n4x
                "50d" -> R.drawable._50d4x
                "50n" -> R.drawable._50n4x
                else -> R.drawable.ic_weather_placeholder
            }
        }
    }

}