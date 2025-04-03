package com.example.forecanow.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.forecanow.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.home.viewModel.HomeViewModel
import com.example.forecanow.home.viewModel.HomeViewModelFactory
import com.example.forecanow.model.Response
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp
import com.example.forecanow.setting.SettingsViewModel
import com.example.forecanow.setting.SettingsViewModelFactory
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.forecanow.home.view.DailyForecastItem
import com.example.forecanow.home.view.HourlyForecastItem
import com.example.forecanow.home.view.WeatherDetailCard
import com.example.forecanow.home.view.WeatherIcon
import com.example.forecanow.home.view.convertWindSpeed
import com.example.forecanow.home.view.extractDailyForecast
import com.example.forecanow.home.view.formatTime
import com.example.forecanow.home.view.getArabicWeatherDescription
import com.example.forecanow.home.view.getCountryName
import com.example.forecanow.home.view.getPressureUnit
import com.example.forecanow.home.view.getTemperatureUnitSymbol
import com.example.forecanow.home.view.getWindSpeedUnitSymbol
import com.example.forecanow.model.ForecastResultResponse
import com.example.forecanow.setting.*
import com.example.forecanow.utils.LocalizationHelper
import java.text.SimpleDateFormat
import java.util.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun FavoriteDetailsScreen(
    favoriteId: Int,
    onBackClick: () -> Unit,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    ),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    )
) {
    val context = LocalContext.current
    var favorite by remember { mutableStateOf<FavoriteLocation?>(null) }
    val settings by settingsViewModel.settings.collectAsState()


    LaunchedEffect(favoriteId) {
        favorite = viewModel.repository.getFavoriteById(favoriteId)
    }


    val apiUnits by remember(settings.temperatureUnit) {
        derivedStateOf {
            when(settings.temperatureUnit) {
                TemperatureUnit.CELSIUS -> "metric"
                TemperatureUnit.FAHRENHEIT -> "imperial"
                TemperatureUnit.KELVIN -> "standard"
            }
        }
    }


    LaunchedEffect(favorite, apiUnits) {
        favorite?.let {
            viewModel.getCurrentWeather(it.lat, it.lon, apiUnits)
            viewModel.getHourlyForecast(it.lat, it.lon, apiUnits)
        }
    }

    val weatherState by viewModel.weather.collectAsState()
    val forecastState by viewModel.forecast.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color(0xFFF5F7FA)
    ) { padding ->
        when {
            favorite == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            weatherState is Response.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            weatherState is Response.Success -> {
                val weatherData = (weatherState as Response.Success).data
                val temp = weatherData.main.temp.toInt()
                val humidity = weatherData.main.humidity
                val windSpeed = convertWindSpeed(weatherData.wind.speed, settings.windSpeedUnit)
                val sunrise = weatherData.sys.sunrise
                val sunset = weatherData.sys.sunset
                val pressure = weatherData.main.pressure
                val feelsLike = weatherData.main.feels_like
                val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
                val iconCode = weatherData.weather.firstOrNull()?.icon ?: "01d"

                val displayTemp = when(settings.temperatureUnit) {
                    TemperatureUnit.CELSIUS -> temp
                    TemperatureUnit.FAHRENHEIT -> temp * 9/5 + 32
                    TemperatureUnit.KELVIN -> temp + 273.15
                }.toInt()


                val temperatureUnitSymbol = getTemperatureUnitSymbol(settings.temperatureUnit)
                val windSpeedUnitSymbol = getWindSpeedUnitSymbol(settings.windSpeedUnit)
                val pressureUnit = getPressureUnit()

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
                                text = if (LocalizationHelper.isArabicLanguage(context)) {
                                    "${weatherData.name}, ${getCountryName(weatherData.sys.country, context)}"
                                } else {
                                    "${weatherData.name}, ${weatherData.sys.country}"
                                },
                                style = MaterialTheme.typography.headlineMedium,
                                color = Color(0xFF2D3748),
                                fontWeight = FontWeight.Bold
                            )

                            Text(
                                text = if (LocalizationHelper.isArabicLanguage(context)) {
                                    SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale("ar")).format(Date())
                                } else {
                                    SimpleDateFormat("EEEE, dd MMMM yyyy - hh:mm a", Locale.getDefault()).format(Date())
                                },
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
                                        text = LocalizationHelper.convertToArabicNumbers("$temp$temperatureUnitSymbol", context),
                                        style = MaterialTheme.typography.displayMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2D3748)
                                    )

                                    WeatherIcon(
                                        iconCode = iconCode,
                                        modifier = Modifier.size(120.dp)
                                    )
                                }

                                Text(
                                    text = if (LocalizationHelper.isArabicLanguage(context)) {
                                        getArabicWeatherDescription(description).replaceFirstChar { it }
                                    } else {
                                        description.replaceFirstChar { it.titlecase() }
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color(0xFF4A5568),
                                    modifier = Modifier.padding(top = 8.dp)
                                )

                                Text(
                                    text = "${stringResource(R.string.feels_like_c)} ${feelsLike.toInt()}${temperatureUnitSymbol}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color(0xFF718096)
                                )
                            }
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.humidity,
                                    title = stringResource(R.string.humidity),
                                    value = "$humidity",
                                    unit = "%",
                                    color = Color(0xFF4299E1)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.air,
                                    title = stringResource(R.string.wind_speed),
                                    value = windSpeed.toString(),
                                    unit = windSpeedUnitSymbol,
                                    color = Color(0xFF38B2AC)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.compress,
                                    title = stringResource(R.string.pressure),
                                    value = pressure.toString(),
                                    unit = pressureUnit,
                                    color = Color(0xFF9F7AEA)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.water_lux,
                                    title = stringResource(R.string.sunrise),
                                    value = formatTime(sunrise, context),
                                    unit = "",
                                    color = Color(0xFFED8936)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.wb_twilight,
                                    title = stringResource(R.string.sunset),
                                    value = formatTime(sunset, context),
                                    unit = "",
                                    color = Color(0xFF667EEA)
                                )
                            }
                        }
                    }

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
                                        HourlyForecastItem(item, temperatureUnitSymbol)
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
                                val dailyForecast = extractDailyForecast(forecastData.list, context)

                                Column(
                                    modifier = Modifier.padding(bottom = 24.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    dailyForecast.forEach { item ->
                                        DailyForecastItem(item, temperatureUnitSymbol)
                                    }
                                }
                            }
                            is ForecastResultResponse.Failure -> {
                                Text(
                                    text = stringResource(R.string.error_loading_hourly_forecast),
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
            weatherState is Response.Failure -> {
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
                                favorite?.let {
                                    viewModel.getCurrentWeather(it.lat, it.lon, apiUnits)
                                    viewModel.getHourlyForecast(it.lat, it.lon, apiUnits)
                                }
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



