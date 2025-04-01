package com.example.forecanow.favorite


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.example.forecanow.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forecanow.db.FavoriteLocation
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.favorite.viewModel.FavoriteViewModel
import com.example.forecanow.favorite.viewModel.FavoriteViewModelFactory
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp


@Composable
fun FavoriteScreen(
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
    onNavigateToDetails: (FavoriteLocation) -> Unit,
    onNavigateToMap: () -> Unit
) {
    val favorites by viewModel.favorites.collectAsState()
    val showAddDialog by viewModel.showAddDialog.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.navigateToDetails.collect { favorite ->
            onNavigateToDetails(favorite)
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToMap() },
                containerColor = colorResource(R.color.purple)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Favorite",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        if (favorites.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No favorite locations added yet",
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.purple)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(favorites) { favorite ->
                    FavoriteItem(
                        favorite = favorite,
                        onFavoriteClick = { viewModel.onFavoriteClicked(it) },
                        onDeleteClick = { viewModel.deleteFavorite(it) }
                    )
                    Divider(color = colorResource(R.color.purple_50))
                }
            }
        }
    }

    if (showAddDialog) {
        AddFavoriteDialog(
            onDismiss = { viewModel.dismissAddFavoriteDialog() },
            onConfirm = { location ->
                viewModel.addFavorite(location)
                viewModel.dismissAddFavoriteDialog()
            }
        )
    }
}

@Composable
fun FavoriteItem(
    favorite: FavoriteLocation,
    onFavoriteClick: (FavoriteLocation) -> Unit,
    onDeleteClick: (FavoriteLocation) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onFavoriteClick(favorite) }
            .background(colorResource(R.color.white)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = favorite.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = colorResource(R.color.purple)
                )
                Text(
                    text = favorite.country,
                    style = MaterialTheme.typography.titleMedium,
                    color = colorResource(R.color.purple_50)
                )
                Text(
                    text = "Lat: ${favorite.lat}, Lon: ${favorite.lon}",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorResource(R.color.purple_50)
                )
            }
            IconButton(
                onClick = { onDeleteClick(favorite) }
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = colorResource(R.color.purple)
                )
            }
        }
    }
}

@Composable
fun AddFavoriteDialog(
    onDismiss: () -> Unit,
    onConfirm: (FavoriteLocation) -> Unit
) {
    var locationName by remember { mutableStateOf("") }
    var country by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lon by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Favorite Location") },
        text = {
            Column {
                OutlinedTextField(
                    value = locationName,
                    onValueChange = { locationName = it },
                    label = { Text("Location Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lat,
                    onValueChange = { lat = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lon,
                    onValueChange = { lon = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val favorite = FavoriteLocation(
                            name = locationName,
                            country = country,
                            lat = lat.toDouble(),
                            lon = lon.toDouble()
                        )
                        onConfirm(favorite)
                    } catch (e: NumberFormatException) {
                        // Handle invalid input
                    }
                },
                colors = ButtonDefaults.buttonColors(colorResource(R.color.purple))
            ) {
                Text("Add", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(colorResource(R.color.purple_50))
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}