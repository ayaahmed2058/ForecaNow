package com.example.forecanow

import android.R.attr.shadowColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButtonDefaults.elevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forecanow.alarm.view.AlertScreen
import com.example.forecanow.favorite.view.FavoriteDetailsScreen
import com.example.forecanow.favorite.view.FavoriteScreen
import com.example.forecanow.map.MapScreen
import com.example.forecanow.home.view.HomeScreen
import com.example.forecanow.map.MapMode
import com.example.forecanow.setting.view.SettingsScreen
import com.example.forecanow.utils.LocalizationHelper
import com.example.forecanow.utils.customFontFamily
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Configuration.getInstance().userAgentValue = packageName

        setContent {

                MainScreen()

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val items = listOf(
        stringResource(R.string.home),
        stringResource(R.string.favorite),
        stringResource(R.string.alarm),
        stringResource(R.string.settings)
    )

    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Favorite,
        Icons.Default.Notifications,
        Icons.Default.Settings
    )

    val rotationState by animateFloatAsState(
        targetValue = if (drawerState.isOpen) 180f else 0f
    )

    val iconColor by animateColorAsState(
        targetValue = if (drawerState.isOpen) colorResource(R.color.teal_200) else Color.Black
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .paint(
                            painter = painterResource(id = R.drawable.bg1),
                            contentScale = ContentScale.Crop
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(R.string.menu),
                            fontSize = 20.sp,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.ExtraLight,
                            color = Color.White
                        )

                        items.forEachIndexed { index, item ->
                            NavigationDrawerItem(
                                icon = {
                                    Icon(
                                        icons[index],
                                        contentDescription = item,
                                        tint = colorResource(R.color.teal_700)
                                    )
                                },
                                label = {
                                    Text(
                                        text = item,
                                        color = Color.White
                                    )
                                },
                                selected = false,
                                onClick = {
                                    when (index) {
                                        0 -> navController.navigate("home")
                                        1 -> navController.navigate("favorite")
                                        2 -> navController.navigate("alarm")
                                        3 -> navController.navigate("settings")
                                    }
                                    scope.launch { drawerState.close() }
                                },
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.app_name),
                            color = Color.White,
                            fontFamily = customFontFamily,
                            fontWeight = FontWeight.ExtraLight,
                            fontSize = 20.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorResource(R.color.teal_200),
                        navigationIconContentColor = Color.White,
                        titleContentColor = Color.White
                    ),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = stringResource(R.string.menu),
                                modifier = Modifier.rotate(rotationState),
                                tint = iconColor
                            )
                        }
                    },

                )

            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("home") {
                    HomeScreen(navController = navController)
                }
                composable("favorite") {
                    FavoriteScreen(navController = navController)
                }
                composable("alarm") { AlertScreen() }
                composable("settings") {
                    SettingsScreen(navController = navController)
                }

                composable("map/settings") {
                    MapScreen(
                        navController = navController,
                        mode = MapMode.SETTINGS_LOCATION
                    )
                }

                composable("map/favorites") {
                    MapScreen(
                        navController = navController,
                        mode = MapMode.FAVORITE_LOCATION
                    )
                }

                composable("favoriteDetails/{favoriteId}") { backStackEntry ->
                    val favoriteId = backStackEntry.arguments?.getString("favoriteId")?.toIntOrNull()
                    favoriteId?.let { id ->
                        FavoriteDetailsScreen(
                            favoriteId = id
                        )
                    }
                }
            }
        }
    }
}
