package com.example.forecanow.home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.lazy.items
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import com.bumptech.glide.integration.compose.GlideImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.forecanow.LocationManager
import com.example.forecanow.R
import com.example.forecanow.home.viewModel.HomeViewModel
import com.example.forecanow.home.viewModel.HomeViewModelFactory
import com.example.forecanow.model.ForecastResultResponse
import com.example.forecanow.model.Response
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSource
import com.example.forecanow.pojo.HourlyWeather
import com.example.forecanow.repository.RepositoryImp
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val locationHelper = LocationManager(this, fusedLocationProviderClient)

        setContent {
            val factory = HomeViewModelFactory(
                RepositoryImp.getInstance(WeatherRemoteDataSource(RetrofitHelper.api))
            )

            val viewModel = ViewModelProvider(this, factory).get(HomeViewModel::class.java)

            val currentLocationHelper = remember { locationHelper }
            val currentViewModel = remember { viewModel }

            LaunchedEffect(Unit) {
                fetchLocationAndWeather(currentLocationHelper, currentViewModel)
            }

            HomeScreen(viewModel)

        }
    }

    private fun fetchLocationAndWeather(locationHelper: LocationManager, viewModel: HomeViewModel) {
        if (locationHelper.checkPermissions()) {
            if (locationHelper.isLocationEnabled()) {
                locationHelper.getFreshLocation(
                    onSuccess = { location ->
                        viewModel.getCurrentWeather(location.latitude, location.longitude)
                        viewModel.getHourlyForecast(location.latitude, location.longitude)
                    },
                    onFailure = { error ->
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                )
            } else {
                Toast.makeText(this, "Turn on Location Services", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Location permissions not granted", Toast.LENGTH_SHORT).show()
        }
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {

    val weatherState by viewModel.weather.collectAsState()

    when (weatherState) {
        is Response.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Response.Success -> {
            val weatherData = (weatherState as Response.Success).data
            val temp = weatherData.main.temp.toInt()
            val humidity = weatherData.main.humidity
            val windSpeed = weatherData.wind.speed
            val sunrise = weatherData.sys.sunrise
            val sunset = weatherData.sys.sunset
            val pressure = weatherData.main.pressure
            val feelsLike = weatherData.main.feels_like
            val main = weatherData.main
            val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
            val iconCode = weatherData.weather.firstOrNull()?.icon ?: "01d"
            Log.d("WeatherIcon", "Icon Code: $iconCode")
            Log.d("WeatherIcon", "Icon URL: https://openweathermap.org/img/wn/${iconCode}@2x.png")

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = R.color.white))
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${weatherData.name} , ${weatherData.sys.country}",
                        fontSize = 24.sp,
                        color = colorResource(R.color.purple),
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val currentDateTime = SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(Date())

                    Text(
                        text = currentDateTime,
                        fontSize = 12.sp,
                        color = colorResource(R.color.purple_50)
                    )


                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$temp°C",
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Bold,
                                color = colorResource(R.color.purple)
                            )

                            Row {
                                GlideImage(
                                    model = "https://openweathermap.org/img/wn/${iconCode}@2x.png",
                                    //model = "https://openweathermap.org/img/wn/${iconCode}.png",
                                    contentDescription = "Weather Icon",
                                    modifier = Modifier.size(100.dp)

                                )

                                Text(text = description, fontSize = 18.sp, color = colorResource(R.color.purple))
                                Text(text = "Feels Like $feelsLike°C", fontSize = 18.sp, color =colorResource(R.color.purple))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(R.color.purple_50),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherDetailItem("Wind", "$windSpeed km/h")
                            WeatherDetailItem("Humidity", "$humidity%")
                            WeatherDetailItem("Pressure", "$pressure")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        )  {
                            WeatherDetailItem("temp_min", "${main.temp_min}")
                            WeatherDetailItem("temp_max", "${main.temp_max}")
                            WeatherDetailItem("deg", "${weatherData.wind.deg}")
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherDetailItem("Sunset", formatTime(sunset))
                            WeatherDetailItem("Sunrise", formatTime(sunrise))
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    ForecastItem(viewModel)
                    ForecastFiveDays(viewModel)
                }
            }



        }

        is Response.Failure -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error: ${(weatherState as Response.Failure).error.message}",
                    color = Color.Red,
                    fontSize = 18.sp
                )
            }
        }

    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ForecastItem(viewModel: HomeViewModel) {

    val forecastState by viewModel.forecast.collectAsState()

    when (forecastState) {
        is ForecastResultResponse.forecastSuccess -> {
            val forecastData = (forecastState as ForecastResultResponse.forecastSuccess).data
            val hourlyData = forecastData.list.take(6)

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hourlyData) { item ->
                    val time = formatTime(item.dt)
                    val temperature = item.main.temp.toInt()
                    val icon = item.weather.firstOrNull()?.icon ?: "01d"

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(
                               color = colorResource(R.color.purple_50),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(8.dp)
                    ) {
                        Text(text = time, fontSize = 14.sp, color = Color.White)
                        GlideImage(
                            model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(40.dp)
                        )
                        Text(text = "$temperature°C", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }

        }

        is ForecastResultResponse.Failure -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error: ${(forecastState as ForecastResultResponse.Failure).error.message}",
                    color = Color.Red,
                    fontSize = 18.sp
                )
            }
        }
        ForecastResultResponse.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> Log.i("TAG", "ForecastItem: error")
    }

}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ForecastFiveDays(viewModel: HomeViewModel) {
    val forecastState by viewModel.forecast.collectAsState()

    when (forecastState) {
        is ForecastResultResponse.forecastSuccess -> {
            val forecastData = (forecastState as ForecastResultResponse.forecastSuccess).data
            val dailyForecast = extractDailyForecast(forecastData.list)

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                items(dailyForecast) { item ->
                    val date = formatDate(item.dt)
                    val temperature = item.main.temp.toInt()
                    val icon = item.weather.firstOrNull()?.icon ?: "01d"

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = colorResource(R.color.purple_50),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = date,
                            fontSize = 14.sp,
                            color = Color.White
                        )

                        GlideImage(
                            model = "https://openweathermap.org/img/wn/${icon}@2x.png",
                            contentDescription = "Weather Icon",
                            modifier = Modifier.size(40.dp)
                        )

                        Text(
                            text = "$temperature°C",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        is ForecastResultResponse.Failure -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Error: ${(forecastState as ForecastResultResponse.Failure).error.message}",
                    color = Color.Red,
                    fontSize = 18.sp
                )
            }
        }

        ForecastResultResponse.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        else -> Log.i("TAG", "ForecastItem: error")
    }
}


@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 14.sp, color = Color.White)
        Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}


fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(timestamp * 1000))
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, dd MMM", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(timestamp * 1000))
}


fun extractDailyForecast(hourlyList: List<HourlyWeather>): List<HourlyWeather> {
    val dailyMap = mutableMapOf<String, HourlyWeather>()

    for (item in hourlyList) {
        val date = formatDate(item.dt)
        if (!dailyMap.containsKey(date)) {
            dailyMap[date] = item
        }
    }

    return dailyMap.values.toList()
}

