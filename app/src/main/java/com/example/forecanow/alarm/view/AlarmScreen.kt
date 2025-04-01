package com.example.forecanow.alarm.view


import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import android.app.DatePickerDialog
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forecanow.R
import com.example.forecanow.alarm.AlarmViewModelFactory
import com.example.forecanow.alarm.AlertViewModel
import com.example.forecanow.alarm.model.WeatherAlert
import com.example.forecanow.db.WeatherDatabase
import com.example.forecanow.db.WeatherLocalDataSourceInterfaceImp
import com.example.forecanow.network.RetrofitHelper
import com.example.forecanow.network.WeatherRemoteDataSourceImp
import com.example.forecanow.repository.RepositoryImp
import java.text.SimpleDateFormat
import java.util.*


@Composable
fun AlertScreen(viewModel: AlertViewModel = viewModel(factory = AlarmViewModelFactory(
    RepositoryImp.getInstance(WeatherRemoteDataSourceImp(RetrofitHelper.api),
        WeatherLocalDataSourceInterfaceImp(WeatherDatabase.getDatabase(LocalContext.current).weatherDao()))
))) {

    val alerts by viewModel.alerts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert))
            }
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.padding(paddingValues)) {
            items(alerts) { alert ->
                AlertItem(alert, onDelete = { viewModel.removeAlert(alert,context) })
            }
        }
    }

    if (showDialog) {
        AlertInputDialog(
            onDismiss = { showDialog = false },
            onConfirm = {startTime, endTime, alertType ->
                viewModel.addAlert(startTime, endTime, alertType, context)
                showDialog = false
            }
        )
    }
}



@Composable
fun AlertInputDialog(onDismiss: () -> Unit, onConfirm: (Long, Long, String) -> Unit) {
    var type by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }

    val alertTypeDefault = stringResource(R.string.notification)
    var alertType by remember { mutableStateOf(alertTypeDefault) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_weather_alert)) },
        text = {
            Column {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text(stringResource(R.string.alert_type)) }
                )
                Spacer(modifier = Modifier.height(8.dp))

                DatePickerButton(stringResource(R.string.select_start_time)) { time -> startTime = time }
                DatePickerButton(stringResource(R.string.select_end_time)) { time -> endTime = time }
                Spacer(modifier = Modifier.height(8.dp))

                Text(stringResource(R.string.choose_alert_method))
                Row {
                    RadioButton(
                        selected = alertType == stringResource(R.string.notification),
                        onClick = { alertType ="Notification" }
                    )
                    Text(stringResource(R.string.notification))

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = alertType == stringResource(R.string.alarm),
                        onClick = { alertType = "Alarm" }
                    )
                    Text(stringResource(R.string.alarm))
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(startTime, endTime, alertType) }) {
                Text(stringResource(R.string.add))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun DatePickerButton(label: String, onTimeSelected: (Long) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    Button(onClick = {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)

                        onTimeSelected(calendar.timeInMillis)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }) {
        Text(label)
    }
}

@Composable
fun AlertItem(alert: WeatherAlert, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = stringResource(R.string.from, formatTimestamp(alert.startTime)))
                Text(text = stringResource(R.string.to, formatTimestamp(alert.endTime)))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_alert))
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
