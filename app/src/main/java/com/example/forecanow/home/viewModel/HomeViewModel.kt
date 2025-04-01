package com.example.forecanow.home.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.forecanow.model.Response
import com.example.forecanow.model.ForecastResultResponse
import com.example.forecanow.repository.RepositoryImp
import com.example.forecanow.repository.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (private val repository: RepositoryInterface) : ViewModel() {

    private val mutableWeather =  MutableStateFlow<Response>(Response.Loading)
    val weather = mutableWeather.asStateFlow()

//    private val mutableForecast: MutableLiveData<ForecastResultResponse> = MutableLiveData()
//    val forecast: LiveData<ForecastResultResponse> = mutableForecast

    private val mutableMessage =  MutableSharedFlow<String>()
    val message= mutableMessage.asSharedFlow()

    private val mutableForecast = MutableStateFlow<ForecastResultResponse>(ForecastResultResponse.Loading)
    val forecast = mutableForecast.asStateFlow()

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