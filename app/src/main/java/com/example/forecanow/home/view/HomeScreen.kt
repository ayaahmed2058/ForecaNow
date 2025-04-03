package com.example.forecanow.home.view

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
import com.example.forecanow.setting.*
import com.example.forecanow.utils.LocalizationHelper
import com.example.forecanow.utils.LocationManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.*



@OptIn(ExperimentalMaterial3Api::class)
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
    ),
    navController: NavController
) {
    val context = LocalContext.current
    val fusedLocationProviderClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val locationHelper = remember { LocationManager(context, fusedLocationProviderClient) }
    val settings by settingsViewModel.settings.collectAsState()


    val manualLocation by viewModel.manualLocation.collectAsState()


    val apiUnits by remember(settings.temperatureUnit) {
        derivedStateOf {
            when(settings.temperatureUnit) {
                TemperatureUnit.CELSIUS -> "metric"
                TemperatureUnit.FAHRENHEIT -> "imperial"
                TemperatureUnit.KELVIN -> "standard"
            }
        }
    }

    fun fetchWeather(lat: Double, lon: Double) {
        viewModel.getCurrentWeather(lat, lon, apiUnits)
        viewModel.getHourlyForecast(lat, lon, apiUnits)
    }

    val selectedLocationLiveData = remember {
        navController.currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<LatLng?>("selected_location")
    }


    LaunchedEffect(settings.locationSource) {
        when (settings.locationSource) {
            LocationSource.GPS -> {
                fetchLocationAndWeather(locationHelper, { location ->
                    fetchWeather(location.latitude, location.longitude)
                }, context)
            }
            LocationSource.OPEN_STREET_MAP -> {
                manualLocation?.let {
                    fetchWeather(it.latitude, it.longitude)
                }
            }
        }
    }


    var forceRefresh by remember { mutableStateOf(false) }
    val selectedLocation by selectedLocationLiveData?.observeAsState() ?: remember { mutableStateOf(null) }


    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { latLng ->
            val newLocation = GeoPoint(latLng.latitude, latLng.longitude)
            viewModel.updateManualLocation(newLocation)
            settingsViewModel.updateLocationSource(LocationSource.OPEN_STREET_MAP)
            fetchWeather(latLng.latitude, latLng.longitude)

            navController.currentBackStackEntry
                ?.savedStateHandle
                ?.remove<LatLng>("selected_location")
        }
    }


    LaunchedEffect(Unit) {
        settingsViewModel.loadInitialSettings()
        if (settings.locationSource == LocationSource.GPS) {
            fetchLocationAndWeather(locationHelper, { location ->
                fetchWeather(location.latitude, location.longitude)
            }, context)
        }
    }

    LaunchedEffect(manualLocation) {
        manualLocation?.let { location ->
            fetchWeather(location.latitude, location.longitude)
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
                val windSpeed = convertWindSpeed(weatherData.wind.speed, settings.windSpeedUnit)
                val sunrise = weatherData.sys.sunrise
                val sunset = weatherData.sys.sunset
                val pressure = weatherData.main.pressure
                val feelsLike = weatherData.main.feels_like
                val description = weatherData.weather.firstOrNull()?.description ?: "N/A"
                val iconCode = weatherData.weather.firstOrNull()?.icon ?: "01d"

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
                                        text = LocalizationHelper.convertToArabicNumbers("$temp$temperatureUnitSymbol",context),
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
                                    value = formatTime(sunrise,context),
                                    unit = "",
                                    color = Color(0xFFED8936)
                                )
                            }

                            item {
                                WeatherDetailCard(
                                    icon = R.drawable.wb_twilight,
                                    title = stringResource(R.string.sunset),
                                    value = formatTime(sunset,context),
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
                                locationHelper.getFreshLocation(
                                    onSuccess = { location ->  fetchWeather(location.latitude, location.longitude) },
                                    onFailure = { error ->
                                        Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                                    }
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
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
            Text(text = title, style = MaterialTheme.typography.bodySmall)
            Text(
                text = LocalizationHelper.convertToArabicNumbers("$value$unit",context),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun HourlyForecastItem(item: HourlyWeather, temperatureUnit: String) {
    val context = LocalContext.current
    val time = formatTime(item.dt, context)
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

            WeatherIcon(
                iconCode = icon,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = LocalizationHelper.convertToArabicNumbers("$temperature$temperatureUnit",context),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
        }
    }
}

@Composable
fun DailyForecastItem(item: HourlyWeather, temperatureUnit: String) {
    val context = LocalContext.current
    val date = formatDate(item.dt, context)
    val temp = item.main.temp.toInt()
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

            WeatherIcon(
                iconCode = icon,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = LocalizationHelper.convertToArabicNumbers("$temp$temperatureUnit", context),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2D3748)
            )
        }
    }
}

@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data("https://openweathermap.org/img/wn/${iconCode}@2x.png")
            .crossfade(true)
            .build(),
        contentDescription = "Weather icon",
        modifier = modifier,
    )
}

private fun fetchLocationAndWeather(
    locationHelper: LocationManager,
    onSuccess: (Location) -> Unit,
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

 fun convertWindSpeed(speed: Double, unit: WindSpeedUnit): String {
    val convertedSpeed = when (unit) {
        WindSpeedUnit.METERS_PER_SECOND -> speed
        WindSpeedUnit.MILES_PER_HOUR -> speed * 2.23694
    }
    return "%.1f".format(convertedSpeed)
}

@Composable
 fun formatTime(timestamp: Long, context: Context): String {
    val time = if (LocalizationHelper.isArabicLanguage()) {
        SimpleDateFormat("hh:mm a", Locale("ar")).format(Date(timestamp * 1000))
    } else {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp * 1000))
    }
    return LocalizationHelper.convertToArabicNumbers(time,context)
}

@Composable
 fun formatDate(timestamp: Long, context: Context): String {
    val date = if (LocalizationHelper.isArabicLanguage()) {
        SimpleDateFormat("EEE, MMM dd", Locale("ar")).format(Date(timestamp * 1000))
    } else {
        SimpleDateFormat("EEE, MMM dd", Locale.getDefault()).format(Date(timestamp * 1000))
    }
    return LocalizationHelper.convertToArabicNumbers(date,context)
}

 fun extractDailyForecast(hourlyList: List<HourlyWeather>, context: Context): List<HourlyWeather> {
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time

    return hourlyList
        .filter { item ->
            val itemDate = Date(item.dt * 1000)
            !isSameDay(currentDate, itemDate)
        }
        .groupBy { item ->
            if (LocalizationHelper.isArabicLanguage(context)) {
                SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
                    .format(Date(item.dt * 1000))
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(item.dt * 1000))
            }
        }
        .map { (_, items) ->
            items.maxByOrNull { it.main.temp } ?: items.first()
        }
        .sortedBy { it.dt }
        .take(5)
}

private fun isSameDay(date1: Date, date2: Date): Boolean {
    val cal1 = Calendar.getInstance().apply { time = date1 }
    val cal2 = Calendar.getInstance().apply { time = date2 }
    return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
            cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)
}


@Composable
fun getArabicWeatherDescription(description: String): String {
    return when (description.lowercase(Locale.getDefault())) {
        "clear sky" -> stringResource(R.string.clear_sky)
        "few clouds" -> stringResource(R.string.few_clouds)
        "scattered clouds" -> stringResource(R.string.scattered_clouds)
        "broken clouds" -> stringResource(R.string.broken_clouds)
        "shower rain" -> stringResource(R.string.shower_rain)
        "rain" -> stringResource(R.string.rain)
        "thunderstorm" -> stringResource(R.string.thunderstorm)
        "snow" -> stringResource(R.string.snow)
        "mist" -> stringResource(R.string.mist)
        else -> description
    }
}

@Composable
fun getTemperatureUnitSymbol(unit: TemperatureUnit): String {
    return when (unit) {
        TemperatureUnit.CELSIUS -> stringResource(R.string.celsius_symbol)
        TemperatureUnit.FAHRENHEIT -> stringResource(R.string.fahrenheit_symbol)
        TemperatureUnit.KELVIN -> stringResource(R.string.kelvin_symbol)
    }
}

@Composable
fun getWindSpeedUnitSymbol(unit: WindSpeedUnit): String {
    return when (unit) {
        WindSpeedUnit.METERS_PER_SECOND -> stringResource(R.string.meter_per_second)
        WindSpeedUnit.MILES_PER_HOUR -> stringResource(R.string.mile_per_hour)
    }
}

@Composable
fun getPressureUnit(): String {
    return stringResource(R.string.hpa_unit)
}

fun getCountryName(countryCode: String, context: Context): String {
    val resourceName = "country_${countryCode.lowercase()}"
    val resourceId = context.resources.getIdentifier(resourceName, "string", context.packageName)

    return if (resourceId != 0) {
        context.getString(resourceId)
    } else {
        countryCode
    }
}



