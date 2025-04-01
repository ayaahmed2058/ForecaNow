package com.example.forecanow.setting


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.forecanow.repository.RepositoryInterface
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow



class SettingsViewModel(private val repository: RepositoryInterface) : ViewModel() {
    private val _settings = MutableStateFlow(AppSettings())
    val settings = _settings.asStateFlow()

    fun updateTemperatureUnit(unit: TemperatureUnit) {
        _settings.value = _settings.value.copy(temperatureUnit = unit)
    }

    fun updateWindSpeedUnit(unit: WindSpeedUnit) {
        _settings.value = _settings.value.copy(windSpeedUnit = unit)
    }

    fun updateLanguage(language: AppLanguage) {
        _settings.value = _settings.value.copy(language = language)
    }

    fun updateLocationSource(source: LocationSource) {
        _settings.value = _settings.value.copy(locationSource = source)
    }
}

class SettingsViewModelFactory(private val repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repo) as T
    }
}