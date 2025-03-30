package com.example.forecanow.alarm


import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import java.util.Calendar
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import java.util.*




class AlarmScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val factory = AlarmViewModelFactory(
                AlertRepository(AlertDatabase.getDatabase(this).alertDao())
            )

            val viewModel = ViewModelProvider(this, factory).get(AlertViewModel::class.java)

            AlertScreen(viewModel)
        }
    }
}


@Composable
fun AlertScreen(viewModel: AlertViewModel) {
    val alerts by viewModel.alerts.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Alert")
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
            onConfirm = { type, startTime, endTime, alertType ->
                viewModel.addAlert(type, startTime, endTime, alertType, context)
                showDialog = false
            }
        )
    }
}



@Composable
fun AlertInputDialog(onDismiss: () -> Unit, onConfirm: (String, Long, Long, String) -> Unit) {
    var type by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis() + 3600000) }
    var alertType by remember { mutableStateOf("Notification") } // Default

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Weather Alert") },
        text = {
            Column {
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Alert Type") }
                )
                Spacer(modifier = Modifier.height(8.dp))

                DatePickerButton("Select Start Time") { time -> startTime = time }
                DatePickerButton("Select End Time") { time -> endTime = time }
                Spacer(modifier = Modifier.height(8.dp))

                Text("Choose Alert Method:")
                Row {
                    RadioButton(
                        selected = alertType == "Notification",
                        onClick = { alertType = "Notification" }
                    )
                    Text("Notification")

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = alertType == "Alarm",
                        onClick = { alertType = "Alarm" }
                    )
                    Text("Alarm")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(type, startTime, endTime, alertType) }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
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
                Text(text = "From: ${formatTimestamp(alert.startTime)}")
                Text(text = "To: ${formatTimestamp(alert.endTime)}")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Alert")
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
