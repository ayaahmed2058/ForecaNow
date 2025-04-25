package com.example.forecanow.data

import com.example.forecanow.data.pojo.ForecastResponse
import com.example.forecanow.data.pojo.WeatherResponse


sealed class Response {

    data object Loading : Response()
    data class Success (val data : WeatherResponse) : Response()
    data class Failure (val error: Throwable) : Response()
}

sealed class ForecastResultResponse {

    data class forecastSuccess (val data : ForecastResponse) : ForecastResultResponse()
    data object Loading : ForecastResultResponse()
    data class Failure (val error: Throwable) : ForecastResultResponse()
}