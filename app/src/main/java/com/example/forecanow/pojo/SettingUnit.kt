package com.example.forecanow.pojo


data class AppSettings(
    val temperatureUnit: TemperatureUnit = TemperatureUnit.CELSIUS,
    val windSpeedUnit: WindSpeedUnit = WindSpeedUnit.METERS_PER_SECOND,
    val language: AppLanguage = AppLanguage.ENGLISH,
    val locationSource: LocationSource = LocationSource.GPS,
    val selectedLatitude: Double = 0.0,
    val selectedLongitude: Double = 0.0,
    val selectedLocationName: String = ""
)

enum class TemperatureUnit {
    KELVIN, CELSIUS, FAHRENHEIT
}

enum class WindSpeedUnit {
    METERS_PER_SECOND, MILES_PER_HOUR
}

enum class AppLanguage {
    ENGLISH, ARABIC
}

enum class LocationSource {
    GPS, OPEN_STREET_MAP
}