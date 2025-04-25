package com.example.forecanow.utils

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.forecanow.R
import com.example.forecanow.data.pojo.TemperatureUnit
import com.example.forecanow.data.pojo.WindSpeedUnit

class Units {
    companion object{
        @Composable
        fun getTemperatureUnitSymbol(unit: TemperatureUnit): String {
            return when (unit) {
                TemperatureUnit.CELSIUS -> stringResource(R.string.celsius_symbol)
                TemperatureUnit.FAHRENHEIT -> stringResource(R.string.fahrenheit_symbol)
                TemperatureUnit.KELVIN -> stringResource(R.string.kelvin_symbol)
            }
        }

        @Composable
        fun getWindSpeedUnitSymbol(unit: WindSpeedUnit): String {
            return when (unit) {
                WindSpeedUnit.METERS_PER_SECOND -> stringResource(R.string.meter_per_second)
                WindSpeedUnit.MILES_PER_HOUR -> stringResource(R.string.mile_per_hour)
            }
        }

        @Composable
        fun getPressureUnit(): String {
            return stringResource(R.string.hpa_unit)
        }

        fun getCountryName(countryCode: String, context: Context): String {
            val resourceName = "country_${countryCode.lowercase()}"
            val resourceId = context.resources.getIdentifier(resourceName, "string", context.packageName)

            return if (resourceId != 0) {
                context.getString(resourceId)
            } else {
                countryCode
            }
        }
        fun convertWindSpeed(speed: Double, unit: WindSpeedUnit): String {
            val convertedSpeed = when (unit) {
                WindSpeedUnit.METERS_PER_SECOND -> speed
                WindSpeedUnit.MILES_PER_HOUR -> speed * 2.23694
            }
            return "%.1f".format(convertedSpeed)
        }
    }
}