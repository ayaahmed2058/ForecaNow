package com.example.forecanow.favorite.view


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.forecanow.R
import com.example.forecanow.data.db.FavoriteLocation
import com.example.forecanow.data.db.WeatherDatabase
import com.example.forecanow.data.db.WeatherLocalDataSourceImp
import com.example.forecanow.favorite.viewModel.FavoriteViewModel
import com.example.forecanow.favorite.viewModel.FavoriteViewModelFactory
import com.example.forecanow.data.network.RetrofitHelper
import com.example.forecanow.data.network.WeatherRemoteDataSourceImp
import com.example.forecanow.data.repository.RepositoryImp
import com.example.forecanow.utils.customFontFamily


@Composable
fun FavoriteScreen(
    viewModel: FavoriteViewModel = viewModel(
        factory = FavoriteViewModelFactory(
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
    val favorites by viewModel.favorites.observeAsState(emptyList())
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.message.collect{
                message -> Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map/favorites") },
                containerColor = colorResource(R.color.teal_700)
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_favorite), tint = Color.White)
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
                        contentDescription = stringResource(R.string.no_favorites),
                        tint = colorResource(R.color.teal_200),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_favorite_locations_added_yet),
                        style = MaterialTheme.typography.titleLarge,
                        color = colorResource(R.color.teal_200),
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Normal
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
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(text = stringResource(R.string.are_you_sure))
            },
            text = {
                Text(text = stringResource(R.string.do_you_want_to_delete_this_location_from_favorites))
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete(favorite)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.teal_700)
                    )
                ) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showDialog = false
                }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.teal_200)
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = colorResource(R.color.bgColor),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.location),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = favorite.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorResource(R.color.white),
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = favorite.country,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorResource(R.color.white),
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            IconButton(
                onClick = { showDialog = true },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.cancel_1),
                    contentDescription = stringResource(R.string.delete),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
