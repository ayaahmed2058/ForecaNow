package com.example.forecanow.utils

import com.example.forecanow.R

class MapImage {
    companion object {
        fun getWeatherIconRes(iconCode: String): Int {
            return when (iconCode) {
                "01d" -> R.drawable.ic_clear_day
                "01n" -> R.drawable.ic_clear_night
                "02d" -> R.drawable.ic_partly_cloudy_day
                "02n" -> R.drawable.ic_partly_cloudy_night
                "03d", "03n" -> R.drawable.ic_cloudy
                "04d", "04n" -> R.drawable.brokenclouds
                "09d", "09n" -> R.drawable.showerrain
                "10d", "10n" -> R.drawable.ic_rain
                "11d", "11n" -> R.drawable.thunderstorm
                "13d" -> R.drawable.ic_snow_day
                "13n" -> R.drawable.ic_snow_night
                "50d", "50n" -> R.drawable.mist
                else -> R.drawable.ic_weather_placeholder
            }
        }
    }

}