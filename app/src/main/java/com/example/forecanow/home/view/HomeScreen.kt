package com.example.forecanow.home.view

import android.content.Context
import android.widget.Toast
import android.location.Location
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import com.bumptech.glide.integration.compose.GlideImage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.forecanow.LocationManager
import com.example.forecanow.R
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.home.viewModel.HomeViewModel
import com.example.forecanow.home.viewModel.HomeViewModelFactory
import com.example.forecanow.model.ForecastResultResponse
import com.example.forecanow.model.Response
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.pojo.HourlyWeather
import com.example.forecanow.repository.RepositoryImp
import com.example.forecanow.setting.SettingsViewModel
import com.example.forecanow.setting.SettingsViewModelFactory
import com.example.forecanow.setting.TemperatureUnit
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.runtime.getValue



@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    )
    ,
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory (
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    )
){
    val context = LocalContext.current
    val fusedLocationProviderClient =
        remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationHelper = remember { LocationManager(context, fusedLocationProviderClient) }
    val settings by settingsViewModel.settings.collectAsState()

    fun getUnitsString(): String {
        return when(settings.temperatureUnit) {
            TemperatureUnit.CELSIUS -> "metric"
            TemperatureUnit.FAHRENHEIT -> "imperial"
            TemperatureUnit.KELVIN -> "standard"
        }
    }

    fun fetchWeather(location: Location) {
        val units = getUnitsString()
        viewModel.getCurrentWeather(location.latitude, location.longitude, units)
        viewModel.getHourlyForecast(location.latitude, location.longitude, units)
    }

    LaunchedEffect(Unit) {
        fetchLocationAndWeather(locationHelper, { location ->
            fetchWeather(location)
        }, context)
    }

    LaunchedEffect(settings) {
        if (viewModel.weather.value is Response.Success ||
            viewModel.weather.value is Response.Failure) {
            // Only refresh if we already have data (not initial load)
            locationHelper.getFreshLocation(
                onSuccess = { location ->
                    fetchWeather(location)
                },
                onFailure = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        }
    }


    val weatherState by viewModel.weather.collectAsState()
    val forecastState by viewModel.forecast.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        when (weatherState) {
            is Response.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
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
                val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
                val iconCode = weatherData.weather.firstOrNull()?.icon ?: "01d"

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .background(Color(0xFFF5F7FA)),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item {

                        Column(
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Text(
                                text = "${weatherData.name}, ${weatherData.sys.country}",
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF2D3748),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = SimpleDateFormat(
                                    "EEEE, dd MMMM yyyy - hh:mm a",
                                    Locale.getDefault()
                                ).format(Date()),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF718096)
                            )
                        }


                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "$temp°C",
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D3748)
                                    )

                                    GlideImage(
                                        model = "https://openweathermap.org/img/wn/${iconCode}@4x.png",
                                        contentDescription = "Weather icon",
                                        modifier = Modifier.size(100.dp)
                                    )
//                                    WeatherIcon(
//                                        iconCode = iconCode,
//                                        modifier = Modifier.size(120.dp)
//                                    )


                                }

                                Text(
                                    text = description.replaceFirstChar { it.titlecase() },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF4A5568),
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Text(
                                    text = "${stringResource(R.string.feels_like_c)} ${feelsLike.toInt()}°C",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF718096)
                                )
                            }
                        }

                        // Weather Details Grid
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                //.background(colorResource(R.color.purple_50))
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.humidity,
                                    title = stringResource(R.string.humidity),
                                    value = "$humidity%",
                                    unit = "",
                                    color = Color(0xFF4299E1)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.air,
                                    title = stringResource(R.string.wind_speed),
                                    value = windSpeed.toString(),
                                    unit = "km/h",
                                    color = Color(0xFF38B2AC)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.compress,
                                    title = stringResource(R.string.pressure),
                                    value = pressure.toString(),
                                    unit = "hPa",
                                    color = Color(0xFF9F7AEA)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.water_lux,
                                    title = stringResource(R.string.sunrise),
                                    value = formatTime(sunrise),
                                    unit = "",
                                    color = Color(0xFFED8936)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.wb_twilight,
                                    title = stringResource(R.string.sunset),
                                    value = formatTime(sunset),
                                    unit = "",
                                    color = Color(0xFF667EEA)
                                )
                            }
                        }
                    }

                    // Hourly Forecast
                    item {
                        Text(
                            text = stringResource(R.string.hourly_forecast),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF2D3748),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        when (forecastState) {
                            is ForecastResultResponse.forecastSuccess -> {
                                val forecastData =
                                    (forecastState as ForecastResultResponse.forecastSuccess).data
                                val hourlyData = forecastData.list.take(8)

                                LazyRow(
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(hourlyData) { item ->
                                        HourlyForecastItem(item)
                                    }
                                }
                            }

                            is ForecastResultResponse.Failure -> {
                                Text(
                                    text = "Error: ${(forecastState as ForecastResultResponse.Failure).error.message}",
                                    color = Color.Red
                                )
                            }

                            ForecastResultResponse.Loading -> {
                                CircularProgressIndicator()
                            }

                            else -> {}
                        }
                    }

                    // Daily Forecast
                    item {
                        Text(
                            text = stringResource(R.string._5_day_forecast),
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF2D3748),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        when (forecastState) {
                            is ForecastResultResponse.forecastSuccess -> {
                                val forecastData =
                                    (forecastState as ForecastResultResponse.forecastSuccess).data
                                val dailyForecast = extractDailyForecast(forecastData.list)

                                Column(
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    dailyForecast.forEach { item ->
                                        DailyForecastItem(item)
                                    }
                                }
                            }

                            is ForecastResultResponse.Failure -> {
                                Text(
                                    text =stringResource(R.string.error_loading_hourly_forecast),
                                    color = Color.Red
                                )
                            }

                            ForecastResultResponse.Loading -> {
                                CircularProgressIndicator()
                            }

                            else -> {}
                        }
                    }
                }
            }

            is Response.Failure -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.error),
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )

                        Text(
                            text = "Error: ${(weatherState as Response.Failure).error.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Red
                        )

                        Button(
                            onClick = {
                                fetchLocationAndWeather(
                                    locationHelper = locationHelper,
                                    onSuccess = { location ->
                                        val units = getUnitsString()
                                        viewModel.getCurrentWeather(location.latitude, location.longitude, units)
                                        viewModel.getHourlyForecast(location.latitude, location.longitude, units)
                                    },
                                    context = context
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4299E1)
                            )
                        ) {
                            Text(stringResource(R.string.retry))
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun WeatherDetailCard(
    icon: Int,
    title: String,
    value: String,
    unit: String,
    color: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096))

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748))

            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF718096))
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun HourlyForecastItem(item: HourlyWeather) {
    val time = formatTime(item.dt)
    val temperature = item.main.temp.toInt()
    val icon = item.weather.firstOrNull()?.icon ?: "01d"

    Card(
        modifier = Modifier.width(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF718096)
            )

//            GlideImage(
//                model = "https://openweathermap.org/img/wn/${icon}@2x.png",
//                contentDescription = "Weather icon",
//                modifier = Modifier.size(40.dp)
//            )
            WeatherIcon(
                iconCode = icon,
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "$temperature°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DailyForecastItem(item: HourlyWeather) {
    val date = formatDate(item.dt)
    val maxTemp = item.main.temp.toInt()
    val icon = item.weather.firstOrNull()?.icon ?: "01d"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF2D3748),
                modifier = Modifier.weight(1f)
            )

//            GlideImage(
//                model = "https://openweathermap.org/img/wn/${icon}@2x.png",
//                contentDescription = "Weather icon",
//                modifier = Modifier.size(40.dp)
//            )
            WeatherIcon(
                iconCode = icon,
                modifier = Modifier.size(120.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$maxTemp°",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2D3748)
                )


            }
        }
    }
}

// Helper functions
fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(timestamp * 1000))
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("EEE, MMM dd", Locale.getDefault())
    sdf.timeZone = TimeZone.getDefault()
    return sdf.format(Date(timestamp * 1000))
}

fun extractDailyForecast(hourlyList: List<HourlyWeather>): List<HourlyWeather> {
    return hourlyList
        .groupBy { item ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(item.dt * 1000))
        }
        .map { (_, items) ->
            items.maxByOrNull { it.main.temp } ?: items.first()
        }
        .sortedBy { it.dt }
}

private fun fetchLocationAndWeather(
    locationHelper: LocationManager,
    onSuccess: (android.location.Location) -> Unit,
    context: Context
) {
    if (locationHelper.checkPermissions()) {
        if (locationHelper.isLocationEnabled()) {
            locationHelper.getFreshLocation(
                onSuccess = onSuccess,
                onFailure = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                }
            )
        } else {
            Toast.makeText(
                context,
                context.getString(R.string.turn_on_location_services),
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
        Toast.makeText(
            context,
            context.getString(R.string.location_permissions_not_granted),
            Toast.LENGTH_SHORT
        ).show()
    }
}

@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier.size(100.dp)
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://openweathermap.org/img/wn/${iconCode}@4x.png")
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = modifier,
    )
}



