package com.example.forecanow.alarm.view


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.Alignment
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import java.util.Calendar
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.forecanow.R
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import java.util.Date
import java.util.Locale
import android.widget.Toast
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import com.example.forecanow.alarm.viewModel.AlarmViewModelFactory
import com.example.forecanow.alarm.viewModel.AlertViewModel
import com.example.forecanow.data.db.WeatherAlert
import com.example.forecanow.data.db.WeatherDatabase
import com.example.forecanow.data.db.WeatherLocalDataSourceImp
import com.example.forecanow.data.network.RetrofitHelper
import com.example.forecanow.data.network.WeatherRemoteDataSourceImp
import com.example.forecanow.data.repository.RepositoryImp
import java.text.SimpleDateFormat
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import com.example.forecanow.utils.customFontFamily


@Composable
fun AlertScreen(viewModel: AlertViewModel = viewModel(factory = AlarmViewModelFactory(
    RepositoryImp.getInstance(WeatherRemoteDataSourceImp(RetrofitHelper.api),
        WeatherLocalDataSourceImp(WeatherDatabase.getDatabase(LocalContext.current).weatherDao()))
))) {

    val alerts by viewModel.alerts.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.message.collect{
                message -> Toast.makeText(context,message,Toast.LENGTH_LONG).show()
        }
    }


    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true },
                containerColor = colorResource(R.color.teal_700)) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_alert) , tint = Color.White)
            }
        }
    ) { paddingValues ->
        if (alerts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.no_alerts),
                        tint = colorResource(R.color.teal_200),
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = stringResource(R.string.no_alert_added_yet),
                        style = MaterialTheme.typography.titleLarge,
                        color = colorResource(R.color.teal_200),
                        fontFamily = customFontFamily,
                        fontWeight = FontWeight.Normal
                    )
                }
            }
        }
        else {
            LazyColumn(modifier = Modifier.padding(paddingValues)) {
                items(alerts) { alert ->
                    AlertItem(alert, onDelete = { viewModel.removeAlert(alert, context) })
                }
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
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
    var alertType by remember { mutableStateOf("Notification") }
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.add_weather_alert)) },
        text = {
            Column {
                DateTimePicker(stringResource(R.string.start_time), startTime) { startTime = it }
                DateTimePicker(stringResource(R.string.end_time), endTime) { endTime = it }

                Spacer(modifier = Modifier.height(16.dp))

                Text(stringResource(R.string.alert_type))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = alertType == "Notification",
                        onClick = { alertType = "Notification" }
                    )
                    Text(stringResource(R.string.notification), modifier = Modifier.padding(end = 16.dp))

                    RadioButton(
                        selected = alertType == "Alarm",
                        onClick = { alertType = "Alarm" }
                    )
                    Text(stringResource(R.string.alarm))
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (endTime > startTime) {
                    onConfirm(startTime, endTime, alertType)
                } else {
                    Toast.makeText(context,
                        context.getString(R.string.end_time_must_be_after_start_time), Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(stringResource(R.string.add_alert))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimePicker(label: String, initialTime: Long, onTimeSelected: (Long) -> Unit) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf(initialTime) }

    val calendar = remember { Calendar.getInstance().apply { timeInMillis = initialTime } }
    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val timeFormatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column {
        Text(label, style = MaterialTheme.typography.labelMedium)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = dateFormatter.format(Date(selectedTime)),
                modifier = Modifier
                    .clickable { showDialog = true }
                    .padding(8.dp)
            )
            Text(
                text = timeFormatter.format(Date(selectedTime)),
                modifier = Modifier
                    .clickable { showDialog = true }
                    .padding(8.dp)
            )
        }
    }

    if (showDialog) {
        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = selectedTime,
            initialDisplayMode = DisplayMode.Picker
        )

        val timeState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE)
        )

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(R.string.select, label)) },
            text = {
                Column {
                    DatePicker(
                        state = dateState,
                        modifier = Modifier.weight(1f)
                    )

                    TimePicker(
                        state = timeState,
                        modifier = Modifier.weight(1f)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val newCalendar = Calendar.getInstance().apply {
                        timeInMillis = dateState.selectedDateMillis ?: selectedTime
                        set(Calendar.HOUR_OF_DAY, timeState.hour)
                        set(Calendar.MINUTE, timeState.minute)
                    }
                    selectedTime = newCalendar.timeInMillis
                    onTimeSelected(selectedTime)
                    showDialog = false
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}


@Composable
fun AlertItem(alert: WeatherAlert, onDelete: () -> Unit) {
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorResource(R.color.teal_200)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatTime(alert.startTime),
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = formatTime(alert.endTime),
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = formatDate(alert.startTime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Normal
                )
                Text(
                    text = formatDate(alert.endTime),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Normal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = alert.alertType,
                    style = MaterialTheme.typography.titleMedium,
                    fontFamily = customFontFamily,
                    fontWeight = FontWeight.Normal
                )

                IconButton(
                    onClick = { showDeleteConfirmation = true },
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

    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onDismiss = { showDeleteConfirmation = false },
            onConfirm = {
                onDelete()
                showDeleteConfirmation = false
            }
        )
    }
}

@Composable
fun DeleteConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.delete_alert)) },
        text = { Text(stringResource(R.string.are_you_sure_delete_alert)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.teal_700)
                )
            ) {
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM.yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

