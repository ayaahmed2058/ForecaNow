package com.example.forecanow.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.sp
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


@Composable
fun FavoriteDetailsScreen(
    favorite: FavoriteLocation,
    viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory (
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    )
)
{
    LaunchedEffect(favorite) {
        viewModel.getCurrentWeather(favorite.lat, favorite.lon)
        viewModel.getHourlyForecast(favorite.lat, favorite.lon)
    }

    // Reuse your HomeScreen composable but with the favorite location's data
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.white))
    ) {

        val weatherState by viewModel.weather.collectAsState()
        when (weatherState) {
            is Response.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is Response.Success -> {
                val weatherData = (weatherState as Response.Success).data
                // Display weather information similar to HomeScreen
                // ...
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
}