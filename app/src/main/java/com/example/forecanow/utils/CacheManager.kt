package com.example.forecanow.utils

import android.content.Context
import com.example.forecanow.pojo.ForecastResponse
import com.example.forecanow.pojo.WeatherResponse
import com.google.gson.Gson

object CacheManager {

    private const val PREF_NAME = "WeatherCache"
    private const val WEATHER_KEY = "weather_data"
    private const val FORECAST_KEY = "forecast_data"

    private fun getSharedPreferences(context: Context) =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)


    fun saveWeather(context: Context, weatherResponse: WeatherResponse) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = Gson().toJson(weatherResponse)
        editor.putString(WEATHER_KEY, json)
        editor.apply()
    }


    fun getCachedWeather(context: Context): WeatherResponse? {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(WEATHER_KEY, null)
        return if (json != null) {
            Gson().fromJson(json, WeatherResponse::class.java)
        } else {
            null
        }
    }


    fun saveForecast(context: Context, forecastResponse: ForecastResponse) {
        val prefs = getSharedPreferences(context)
        val editor = prefs.edit()
        val json = Gson().toJson(forecastResponse)
        editor.putString(FORECAST_KEY, json)
        editor.apply()
    }


    fun getCachedForecast(context: Context): ForecastResponse? {
        val prefs = getSharedPreferences(context)
        val json = prefs.getString(FORECAST_KEY, null)
        return if (json != null) {
            Gson().fromJson(json, ForecastResponse::class.java)
        } else {
            null
        }
    }
}
