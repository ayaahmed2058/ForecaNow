package com.example.forecanow.favorite

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay
import android.view.MotionEvent
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.favorite.viewModel.FavoriteViewModel
import com.example.forecanow.favorite.viewModel.FavoriteViewModelFactory
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.forecanow.R
import com.example.forecanow.db.FavoriteLocation
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    ),
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedMarker by remember { mutableStateOf<GeoPoint?>(null) }
    var locationName by remember { mutableStateOf("") }

    // For search functionality
    var searchQuery by remember { mutableStateOf("") }
    val searchResults = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Favorite Location") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedMarker?.let { point ->
                        viewModel.addFavorite(
                            FavoriteLocation(
                                name = locationName.ifEmpty { "Custom Location" },
                                country = "", // You might want to reverse geocode this
                                lat = point.latitude,
                                lon = point.longitude
                            )
                        )
                        onBack()
                    } ?: run {
                        Toast.makeText(context, "Please select a location first", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = colorResource(R.color.purple)
            ) {
                Icon(Icons.Default.Done, contentDescription = "Save Location")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    // Here you would call your geocoding service
                    // For now we'll just simulate some results
                    if (it.length > 2) {
                        searchResults.clear()
                        searchResults.addAll(listOf("$it City 1", "$it City 2", "$it Town"))
                    }
                },
                label = { Text("Search location") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                }
            )

            // Search Results
            if (searchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 200.dp)
                ) {
                    items(searchResults) { result ->
                        Text(
                            text = result,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // In a real app, you would geocode this to coordinates
                                    // For now we'll just set some random coordinates
                                    selectedMarker = GeoPoint(
                                        30.0 + Random.nextDouble() * 10.0,
                                        30.0 + Random.nextDouble() * 10.0
                                    )
                                    locationName = result
                                    searchResults.clear()
                                    searchQuery = result
                                }
                                .padding(16.dp)
                        )
                        Divider()
                    }
                }
            }

            // Location Name Input
            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Location Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )

            // OSMDroid MapView (Android View interop)
            AndroidView(
                factory = { ctx ->
                    MapView(ctx).apply {
                        setTileSource(TileSourceFactory.MAPNIK)
                        setMultiTouchControls(true)

                        controller.setZoom(12.0)
                        val startPoint = GeoPoint(30.0444, 31.2357) // Cairo coordinates
                        controller.setCenter(startPoint)

                        overlays.add(object : Overlay() {
                            override fun onLongPress(
                                event: MotionEvent,
                                mapView: MapView
                            ): Boolean {
                                val geoPoint = mapView.projection.fromPixels(
                                    event.x.toInt(),
                                    event.y.toInt()
                                )
                                selectedMarker = geoPoint as GeoPoint?
                                if (locationName.isEmpty()) {
                                    locationName = "Custom Location"
                                }
                                return true
                            }
                        })
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            )

            // Selected Location Info
            selectedMarker?.let { point ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text("Selected Location:", fontWeight = FontWeight.Bold)
                    Text("Latitude: ${point.latitude}")
                    Text("Longitude: ${point.longitude}")
                }
            }
        }
    }
}