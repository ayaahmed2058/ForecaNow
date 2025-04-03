package com.example.forecanow.favorite


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.navigation.NavController
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
    navController: NavController
) {
    val favorites by viewModel.favorites.collectAsState()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map") },
                containerColor = Color(0xFF6200EE)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Favorite", tint = Color.White)
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "No Favorites",
                        tint = Color(0xFF6200EE),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "No favorite locations added yet",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF6200EE)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                items(favorites) { favorite ->
                    FavoriteLocationCard(
                        favorite = favorite,
                        onClick = { navController.navigate("favoriteDetails/${favorite.id}") },
                        onDelete = { viewModel.deleteFavorite(it) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteLocationCard(
    favorite: FavoriteLocation,
    onClick: () -> Unit,
    onDelete: (FavoriteLocation) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = favorite.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color(0xFF2D3748)
                )
                Text(
                    text = favorite.country,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF718096)
                )
            }

            IconButton(
                onClick = { onDelete(favorite) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color(0xFFE53E3E)
                )
            }
        }
    }
}