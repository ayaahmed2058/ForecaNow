package com.example.forecanow.home.viewModel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecanow.model.Response
import com.example.forecanow.model.ForecastResultResponse
import com.example.forecanow.pojo.LocationData
import com.example.forecanow.pojo.LocationEntity
import com.example.forecanow.repository.RepositoryImp
import com.example.forecanow.repository.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class HomeViewModel ( val repository: RepositoryInterface) : ViewModel() {

    private val mutableWeather =  MutableStateFlow<Response>(Response.Loading)
    val weather = mutableWeather.asStateFlow()

//    private val mutableForecast: MutableLiveData<ForecastResultResponse> = MutableLiveData()
//    val forecast: LiveData<ForecastResultResponse> = mutableForecast

    private val mutableMessage =  MutableSharedFlow<String>()
    val message= mutableMessage.asSharedFlow()

    private val mutableForecast = MutableStateFlow<ForecastResultResponse>(ForecastResultResponse.Loading)
    val forecast = mutableForecast.asStateFlow()

    private val _manualLocation = MutableStateFlow<GeoPoint?>(null)
    val manualLocation: StateFlow<GeoPoint?> = _manualLocation.asStateFlow()

    fun getCurrentWeather(lat: Double, lon: Double, units: String = "metric") {
        viewModelScope.launch {
            try {
                val weatherResponse = repository.getWeather(lat, lon, units)
                weatherResponse
                    .catch { ex ->
                        mutableWeather.value = Response.Failure(ex)
                        mutableMessage.emit("Error From API: ${ex.message}")
                    }
                    .collect {
                        mutableWeather.value = Response.Success(it)
                    }
            } catch (e: Exception) {
                mutableWeather.value = Response.Failure(e)
                mutableMessage.emit("an error occurs ${e.message}")
            }
        }
    }

    fun getHourlyForecast(lat: Double, lon: Double, units: String = "metric") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                repository.getHourlyForecast(lat, lon, units)
                    .catch { ex ->
                        println("Forecast error: ${ex.message}")
                        mutableForecast.value = ForecastResultResponse.Failure(ex)
                        mutableMessage.emit("Error From API: ${ex.message}")
                    }
                    .collect { result ->
                        println("Forecast received: $result")
                        mutableForecast.value = ForecastResultResponse.forecastSuccess(result)

                    }
            } catch (e: Exception) {
                println("Forecast exception: ${e.message}")
                mutableForecast.value = ForecastResultResponse.Failure(e)
                mutableMessage.emit("an error occurred ${e.message}")
            }
        }
    }

    fun updateManualLocation(location: GeoPoint) {
        _manualLocation.value = location
    }

    private val _selectedLocation = MutableStateFlow<LocationData?>(null)
    val selectedLocation: StateFlow<LocationData?> = _selectedLocation.asStateFlow()



    suspend fun getCityName(lat: Double, lon: Double): String {
        return try {
            val response = repository.getWeather(lat, lon, "metric").first()
            response.name
        } catch (e: Exception) {
            "Unknown Location"
        }
    }


    fun setSelectedLocation(lat: Double, lon: Double, name: String) {
        _selectedLocation.value = LocationData(lat, lon, name)
        getCurrentWeather(lat, lon)
        getHourlyForecast(lat, lon)
    }


//    val selectedLocation = settingsViewModel.settings.map {
//        it.selectedLatitude?.let { lat ->
//            it.selectedLongitude?.let { lon ->
//                GeoPoint(lat, lon)
//            }
//        }
//    }.stateIn(viewModelScope, SharingStarted.Lazily, null)
//
//    LaunchedEffect(selectedLocation) {
//        selectedLocation?.let {
//            fetchWeather(it.latitude, it.longitude)
//        }
//    }





//    fun getHourlyForecast(lat: Double, lon: Double) {
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            try {
//                val forecastResponse = repository.getHourlyForecast(lat, lon)
//                forecastResponse
//                    .catch { ex ->
//                        mutableForecast.postValue(ForecastResultResponse.Failure(ex))
//                        mutableMessage.emit("Error From API: ${ex.message}")
//                    }
//                    .collect {
//                        mutableForecast.postValue(ForecastResultResponse.forecastSuccess(it))
//                    }
//
//            } catch (e: Exception) {
//                mutableForecast.value = ForecastResultResponse.Failure(e)
//                mutableMessage.emit("an error occurs ${e.message}")
//            }
//        }
//    }


}

class HomeViewModelFactory (private val repo: RepositoryImp):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HomeViewModel(repo) as T
    }
}