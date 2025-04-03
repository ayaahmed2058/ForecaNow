package com.example.forecanow.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.favorite.viewModel.FavoriteViewModel
import com.example.forecanow.favorite.viewModel.FavoriteViewModelFactory
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.first


@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun MapScreen(
    navController: NavController,
    viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    )
) {

    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var cityName by remember { mutableStateOf("") }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    LaunchedEffect(selectedLocation) {
        selectedLocation?.let { latLng ->
            try {
                val response = viewModel.repository.getWeather(
                    latLng.latitude,
                    latLng.longitude,
                    "metric"
                ).first()
                cityName = response.name
            } catch (e: Exception) {
                cityName = "Selected Location"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedLocation != null) {
                        IconButton(
                            onClick = { showConfirmationDialog = true },
                            enabled = cityName.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Confirm",
                                tint = if (cityName.isNotEmpty()) Color(0xFF6200EE) else Color.Gray
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            OSMapView(
                modifier = Modifier.fillMaxSize(),
                onLocationSelected = { latLng ->
                    selectedLocation = latLng
                }
            )

            if (cityName.isNotEmpty()) {
                Text(
                    text = "Selected: $cityName",
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    color = Color.Black
                )
            }
        }
    }

    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Location") },
            text = { Text("Are you sure you want to save this location: $cityName?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedLocation?.let { latLng ->
                            viewModel.addFavorite(
                                FavoriteLocation(
                                    name = cityName,
                                    country = "",
                                    lat = latLng.latitude,
                                    lon = latLng.longitude
                                )
                            )
                            navController.popBackStack()
                        }
                        showConfirmationDialog = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showConfirmationDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}