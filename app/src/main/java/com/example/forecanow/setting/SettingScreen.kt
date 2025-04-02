package com.example.forecanow.setting


import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.os.Build
import android.util.Log
import java.util.Locale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forecanow.R
import androidx.compose.runtime.getValue
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(
            RepositoryImp.getInstance(
                WeatherRemoteDataSourceImp(RetrofitHelper.api),
                WeatherLocalDataSourceInterfaceImp(
                    WeatherDatabase.getDatabase(LocalContext.current).weatherDao()
                )
            )
        )
    ),
    onNavigateToMap: () -> Unit = {}
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsState()

    LaunchedEffect(settings) {
        if (settings.language != AppLanguage.ENGLISH && settings.language != AppLanguage.ARABIC) {
            viewModel.updateLanguage(AppLanguage.ENGLISH)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item { LocationPreferenceSection(settings, viewModel, onNavigateToMap) }
            item { UnitsPreferenceSection(settings, viewModel) }
            item { LanguagePreferenceSection(settings, viewModel, context) }
        }
    }
}


@Composable
private fun SettingsCategory(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun LocationPreference(
    currentSource: LocationSource,
    onSourceSelected: (LocationSource) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ListItem(
                headlineContent = { Text(stringResource(R.string.location_source)) },
                supportingContent = { Text(stringResource(R.string.choose_location_source)) }
            )

            LocationSource.values().forEach { source ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSourceSelected(source) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentSource == source,
                        onClick = { onSourceSelected(source) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when(source) {
                            LocationSource.GPS -> stringResource(R.string.gps_location)
                            LocationSource.MAP -> stringResource(R.string.map_location)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun TemperatureUnitPreference(
    currentUnit: TemperatureUnit,
    onUnitSelected: (TemperatureUnit) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ListItem(
                headlineContent = { Text(stringResource(R.string.temperature_unit)) },
                supportingContent = { Text(stringResource(R.string.choose_temperature_unit)) }
            )

            TemperatureUnit.values().forEach { unit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUnitSelected(unit) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentUnit == unit,
                        onClick = { onUnitSelected(unit) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when(unit) {
                            TemperatureUnit.CELSIUS -> stringResource(R.string.celsius)
                            TemperatureUnit.FAHRENHEIT -> stringResource(R.string.fahrenheit)
                            TemperatureUnit.KELVIN -> stringResource(R.string.kelvin)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun WindSpeedUnitPreference(
    currentUnit: WindSpeedUnit,
    onUnitSelected: (WindSpeedUnit) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ListItem(
                headlineContent = { Text(stringResource(R.string.wind_speed_unit)) },
                supportingContent = { Text(stringResource(R.string.choose_wind_speed_unit)) }
            )

            WindSpeedUnit.values().forEach { unit ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onUnitSelected(unit) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentUnit == unit,
                        onClick = { onUnitSelected(unit) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when(unit) {
                            WindSpeedUnit.METERS_PER_SECOND -> stringResource(R.string.meters_per_second)
                            WindSpeedUnit.MILES_PER_HOUR -> stringResource(R.string.miles_per_hour)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguagePreference(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            ListItem(
                headlineContent = { Text(stringResource(R.string.app_language)) },
                supportingContent = { Text(stringResource(R.string.choose_app_language)) }
            )

            AppLanguage.values().forEach { language ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLanguageSelected(language) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = currentLanguage == language,
                        onClick = { onLanguageSelected(language) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = when(language) {
                            AppLanguage.ENGLISH -> stringResource(R.string.english)
                            AppLanguage.ARABIC -> stringResource(R.string.arabic)
                        },
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationPreferenceSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    onNavigateToMap: () -> Unit
) {
    SettingsCategory(title = stringResource(R.string.location_settings))
    LocationPreference(
        currentSource = settings.locationSource,
        onSourceSelected = { source ->
            if (source == LocationSource.MAP) {
                onNavigateToMap()
            }
            viewModel.updateSettings(settings.copy(locationSource = source))
        }
    )
}

@Composable
private fun UnitsPreferenceSection(
    settings: AppSettings,
    viewModel: SettingsViewModel
) {
    SettingsCategory(title = stringResource(R.string.unit_settings))
    TemperatureUnitPreference(
        currentUnit = settings.temperatureUnit,
        onUnitSelected = { unit ->
            viewModel.updateSettings(settings.copy(temperatureUnit = unit))
        }
    )
    WindSpeedUnitPreference(
        currentUnit = settings.windSpeedUnit,
        onUnitSelected = { unit ->
            viewModel.updateSettings(settings.copy(windSpeedUnit = unit))
        }
    )
}

@Composable
private fun LanguagePreferenceSection(
    settings: AppSettings,
    viewModel: SettingsViewModel,
    context: Context
) {
    SettingsCategory(title = stringResource(R.string.language_settings))
    LanguagePreference(
        currentLanguage = settings.language,
        onLanguageSelected = { language ->
            viewModel.updateSettings(settings.copy(language = language))
            updateLocale(context, language)
        }
    )
}

private fun updateLocale(context: Context, language: AppLanguage) {
    val locale = when (language) {
        AppLanguage.ENGLISH -> Locale("en")
        AppLanguage.ARABIC -> Locale("ar")
    }

    val resources = context.resources
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.createConfigurationContext(configuration)
    }

    resources.updateConfiguration(configuration, resources.displayMetrics)

    if (context is Activity) {
        context.recreate()
    }
}