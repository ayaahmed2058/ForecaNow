package com.example.forecanow.home.view

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forecanow.R
import com.example.forecanow.home.viewModel.HomeViewModel
import com.example.forecanow.home.viewModel.HomeViewModelFactory
import com.example.forecanow.data.ForecastResultResponse
import com.example.forecanow.data.Response
import com.example.forecanow.data.network.RetrofitHelper
import com.example.forecanow.data.network.WeatherRemoteDataSourceImp
import com.example.forecanow.data.repository.RepositoryImp
import com.example.forecanow.utils.LocalizationHelper
import com.example.forecanow.utils.LocationManager
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
import com.example.forecanow.data.db.WeatherDatabase
import com.example.forecanow.data.db.WeatherLocalDataSourceImp
import com.example.forecanow.data.pojo.LocationData
import com.example.forecanow.data.pojo.LocationSource
import com.example.forecanow.data.pojo.TemperatureUnit
import com.example.forecanow.setting.viewModel.SettingsViewModel
import com.example.forecanow.setting.viewModel.SettingsViewModelFactory
import com.example.forecanow.utils.Format.formatTime
import com.example.forecanow.utils.LocalizationHelper.getArabicWeatherDescription
import com.example.forecanow.utils.Units.Companion.convertWindSpeed
import com.example.forecanow.utils.Units.Companion.getCountryName
import com.example.forecanow.utils.Units.Companion.getPressureUnit
import com.example.forecanow.utils.Units.Companion.getTemperatureUnitSymbol
import com.example.forecanow.utils.Units.Companion.getWindSpeedUnitSymbol
import com.example.forecanow.utils.customFontFamily
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    ),
    settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    ),
    navController: NavController
) {
    val context = LocalContext.current
    val locationHelper = remember {
        LocationManager(context, LocationServices.getFusedLocationProviderClient(context))
    }
    val settings by settingsViewModel.settings.collectAsState()
    var showLocationDialog by remember { mutableStateOf(false) }


    val apiUnits = remember(settings.temperatureUnit) {
        when (settings.temperatureUnit) {
            TemperatureUnit.CELSIUS -> "metric"
            TemperatureUnit.FAHRENHEIT -> "imperial"
            TemperatureUnit.KELVIN -> "standard"
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModel.getCurrentWeather(lat, lon, apiUnits)
        viewModel.getHourlyForecast(lat, lon, apiUnits)
    }

    LaunchedEffect(Unit) {
        settingsViewModel.loadInitialSettings()
        if (!locationHelper.isLocationEnabled()) {
            showLocationDialog = true
        }
    }




    var isManualUpdate by remember { mutableStateOf(false) }

    val locationData = navController.previousBackStackEntry
        ?.savedStateHandle
        ?.getLiveData<LocationData>("selected_location_data")
        ?.observeAsState()

    LaunchedEffect(locationData) {
        locationData?.value?.let { data ->
            isManualUpdate = true
            viewModel.setSelectedLocation(data.lat, data.lon, data.name)
            settingsViewModel.updateSelectedLocation(data.lat, data.lon, data.name)
            fetchWeather(data.lat, data.lon)

            navController.previousBackStackEntry?.savedStateHandle?.remove<LocationData>("selected_location_data")
            delay(1000)
            isManualUpdate = false
        }
    }


    LaunchedEffect(settings.locationSource) {
        if (!isManualUpdate) {
            when (settings.locationSource) {
                LocationSource.GPS -> {
                    if (!locationHelper.isLocationEnabled()) {
                        showLocationDialog = true
                    } else {
                        val success = locationHelper.fetchLocationAndWeather(
                            { location ->
                                if (!isManualUpdate) {
                                    viewModel.setSelectedLocation(location.latitude, location.longitude, "Current Location")
                                    fetchWeather(location.latitude, location.longitude)
                                }
                            },
                            context
                        )
                        if (!success) {
                            showLocationDialog = true
                        }
                    }
                }
                LocationSource.OPEN_STREET_MAP -> {
                    if (!isManualUpdate && settings.selectedLatitude != 0.0) {
                        fetchWeather(settings.selectedLatitude, settings.selectedLongitude)
                    }
                }
            }
        }
    }


    val weatherState by viewModel.weather.collectAsState()
    val forecastState by viewModel.forecast.collectAsState()


    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text(stringResource(R.string.location_required)) },
            text = { Text(stringResource(R.string.please_enable_location)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLocationDialog = false
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) {
                    Text(stringResource(R.string.enable))
                }
            },
            dismissButton = {
                TextButton(onClick = { showLocationDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }



    Scaffold(
        containerColor = colorResource(R.color.scaffoldColor),
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
                val windSpeed = convertWindSpeed(weatherData.wind.speed, settings.windSpeedUnit)
                val sunrise = weatherData.sys.sunrise
                val sunset = weatherData.sys.sunset
                val pressure = weatherData.main.pressure
                val feelsLike = weatherData.main.feels_like
                val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
                val iconCode = weatherData.weather.first()?.icon ?: "01d"
                val cloud = weatherData.clouds.all

                Log.i("TAG", "https://openweathermap.org/img/wn/${iconCode}@4x.png")

                val temperatureUnitSymbol = getTemperatureUnitSymbol(settings.temperatureUnit)
                val windSpeedUnitSymbol = getWindSpeedUnitSymbol(settings.windSpeedUnit)
                val pressureUnit = getPressureUnit()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        //.padding(padding)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.bg),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            //.background(colorResource(R.color.bgColor))
                                ,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        item {
                            Column(
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Text(
                                    text = if (LocalizationHelper.isArabicLanguage(context)) {
                                        "${weatherData.name}, ${
                                            getCountryName(
                                                weatherData.sys.country,
                                                context
                                            )
                                        }"
                                    } else {
                                        "${weatherData.name}, ${weatherData.sys.country}"
                                    },
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = colorResource(R.color.teal_700),
                                    fontFamily = customFontFamily,
                                    fontWeight = FontWeight.ExtraLight
                                )

                                Text(
                                    text = if (LocalizationHelper.isArabicLanguage(context)) {
                                        SimpleDateFormat(
                                            "EEEE, dd MMMM yyyy - hh:mm a",
                                            Locale("ar")
                                        ).format(Date())
                                    } else {
                                        SimpleDateFormat(
                                            "EEEE, dd MMMM yyyy - hh:mm a",
                                            Locale.getDefault()
                                        ).format(Date())
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colorResource(R.color.dateColor),
                                )
                            }

                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                shape = RoundedCornerShape(
                                    topStart = 20.dp,
                                    topEnd = 20.dp,
                                    bottomEnd = 60.dp,
                                    bottomStart = 20.dp
                                ),
                                colors = CardDefaults.cardColors(colorResource(R.color.teal_200)),
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
                                            text = LocalizationHelper.convertToArabicNumbers(
                                                "$temp$temperatureUnitSymbol",
                                                context
                                            ),
                                            style = MaterialTheme.typography.displayMedium,
                                            fontFamily = customFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 56.sp,
                                            color = colorResource(R.color.countryColor)
                                        )

                                        WeatherIcon(
                                            iconCode = iconCode,
                                            modifier = Modifier.size(150.dp)
                                        )
                                    }

                                    Text(
                                        text = if (LocalizationHelper.isArabicLanguage(context)) {
                                            getArabicWeatherDescription(description).replaceFirstChar { it }
                                        } else {
                                            description.replaceFirstChar { it.titlecase() }
                                        },
                                        style = MaterialTheme.typography.titleMedium,
                                        color = colorResource(R.color.cloudColor),
                                        modifier = Modifier.padding(top = 8.dp),
                                        fontFamily = customFontFamily,
                                        fontWeight = FontWeight.Normal
                                    )

                                    Text(
                                        text = "${stringResource(R.string.feels_like_c)} ${feelsLike.toInt()}${temperatureUnitSymbol}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = colorResource(R.color.white),
                                        fontFamily = customFontFamily,
                                        fontWeight = FontWeight.Normal
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
                                        color = colorResource(R.color.teal_200)
                                    )
                                }

                                item {
                                    WeatherDetailCard(
                                        icon = R.drawable.air,
                                        title = stringResource(R.string.wind_speed),
                                        value = windSpeed.toString(),
                                        unit = windSpeedUnitSymbol,
                                        color = colorResource(R.color.teal_200)
                                    )
                                }

                                item {
                                    WeatherDetailCard(
                                        icon = R.drawable.compress,
                                        title = stringResource(R.string.pressure),
                                        value = pressure.toString(),
                                        unit = pressureUnit,
                                        color = colorResource(R.color.teal_200)
                                    )
                                }
                                item {
                                    WeatherDetailCard(
                                        icon = R.drawable.weather,
                                        title = stringResource(R.string.cloud),
                                        value = cloud.toString(),
                                        unit = "%",
                                        color = colorResource(R.color.teal_200)
                                    )
                                }

                                item {
                                    WeatherDetailCard(
                                        icon = R.drawable.water_lux,
                                        title = stringResource(R.string.sunrise),
                                        value = formatTime(sunrise, context),
                                        unit = "",
                                        color = colorResource(R.color.teal_200)
                                    )
                                }

                                item {
                                    WeatherDetailCard(
                                        icon = R.drawable.wb_twilight,
                                        title = stringResource(R.string.sunset),
                                        value = formatTime(sunset, context),
                                        unit = "",
                                        color = colorResource(R.color.teal_200)
                                    )
                                }

                            }
                        }

                        item {
                            Text(
                                text = stringResource(R.string.hourly_forecast),
                                style = MaterialTheme.typography.titleLarge,
                                color = colorResource(R.color.teal_700),
                                fontFamily = customFontFamily,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            when (forecastState) {
                                is ForecastResultResponse.forecastSuccess -> {
                                    val forecastData =
                                        (forecastState as ForecastResultResponse.forecastSuccess).data
                                    val hourlyData = forecastData.list.take(7)

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
                                        color = Color.Red,
                                        modifier = Modifier.padding(16.dp),
                                        fontFamily = customFontFamily,
                                        fontWeight = FontWeight.Normal
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
                                color = colorResource(R.color.teal_700),
                                fontFamily = customFontFamily,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            when (forecastState) {
                                is ForecastResultResponse.forecastSuccess -> {
                                    val forecastData =
                                        (forecastState as ForecastResultResponse.forecastSuccess).data
                                    val dailyForecast =
                                        extractDailyForecast(forecastData.list, context)

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
                                        color = Color.Red,
                                        modifier = Modifier.padding(16.dp),
                                        fontFamily = customFontFamily,
                                        fontWeight = FontWeight.Normal
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

            }

            is Response.Failure -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.internet),
                            contentDescription = stringResource(R.string.error),
                            tint = Color.Red,
                            modifier = Modifier.size(48.dp)
                        )
//
//                        Text(
//                            text = "Error: ${(weatherState as Response.Failure).error.message}",
//                            style = MaterialTheme.typography.bodyLarge,
//                            color = Color.Red,
//                            fontFamily = customFontFamily,
//                            fontWeight = FontWeight.Normal
//                        )

                        Button(
                            onClick = {
                                locationHelper.getFreshLocation(
                                    onSuccess = { location ->  fetchWeather(location.latitude, location.longitude) },
                                    onFailure = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorResource(R.color.teal_700)
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
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(Color.Transparent)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = title,
                tint = color
            )
            Text(text = title, style = MaterialTheme.typography.titleMedium ,  fontFamily = customFontFamily,
                fontWeight = FontWeight.ExtraLight)
            Text(
                text = LocalizationHelper.convertToArabicNumbers("$value$unit",context),
                style = MaterialTheme.typography.bodyLarge,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

