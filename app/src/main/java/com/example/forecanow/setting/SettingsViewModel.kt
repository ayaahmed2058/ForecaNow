package com.example.forecanow.setting


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecanow.repository.RepositoryInterface
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class SettingsViewModel(private val repository: RepositoryInterface) : ViewModel() {
    private val _settings = MutableStateFlow(AppSettings())
    val settings = _settings.asStateFlow()

    init {
        loadInitialSettings()
    }

     fun loadInitialSettings() {
        viewModelScope.launch {
            try {
                repository.getSettings()?.let { savedSettings ->
                    _settings.value = savedSettings
                    applySettings(savedSettings)
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error loading settings", e)
            }
        }
    }

    private fun applySettings(settings: AppSettings) {
        viewModelScope.launch {
            _settings.value = _settings.value.copy(temperatureUnit = settings.temperatureUnit)
            _settings.value = _settings.value.copy(windSpeedUnit = settings.windSpeedUnit)
            _settings.value = _settings.value.copy(language = settings.language)
            _settings.value = _settings.value.copy(locationSource = settings.locationSource)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            try {
                _settings.value = newSettings
                repository.saveSettings(newSettings)
                applySettings(newSettings)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error saving settings", e)
            }
        }
    }


    fun updateLanguage(language: AppLanguage) {
        viewModelScope.launch {
            try {
                val newSettings = _settings.value.copy(language = language)
                _settings.value = newSettings
                repository.saveSettings(newSettings)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error saving language", e)
            }
        }
    }

    private val _manualSelectedLocation = MutableStateFlow<LatLng?>(null)
    val manualSelectedLocation = _manualSelectedLocation.asStateFlow()

    fun updateSelectedLocation(lat: Double, lon: Double, name: String) {
        viewModelScope.launch {
            try {
                val newSettings = _settings.value.copy(
                    selectedLatitude = lat,
                    selectedLongitude = lon,
                    selectedLocationName = name
                )
                _settings.value = newSettings
                repository.saveSettings(newSettings)
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error saving selected location", e)
            }
        }
    }

    fun updateLocationSource(source: LocationSource) {
        viewModelScope.launch {
            try {
                val newSettings = _settings.value.copy(locationSource = source)
                _settings.value = newSettings
                repository.saveSettings(newSettings)

                Log.d("SettingsVM", "Updated location source to: $source")
            } catch (e: Exception) {
                Log.e("SettingsVM", "Error updating location source", e)
            }
        }
    }

}

class SettingsViewModelFactory(private val repo: RepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(repo) as T
    }
}