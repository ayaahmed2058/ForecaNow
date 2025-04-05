package com.example.forecanow.home.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.forecanow.R
import com.example.forecanow.pojo.HourlyWeather
import com.example.forecanow.utils.Format.formatTime
import com.example.forecanow.utils.LocalizationHelper
import com.example.forecanow.utils.customFontFamily

@Composable
fun HourlyForecastItem(item: HourlyWeather, temperatureUnit: String) {
    val context = LocalContext.current
    val time = formatTime(item.dt, context)
    val temperature = item.main.temp.toInt()
    val icon = item.weather.firstOrNull()?.icon ?: "01d"

    Card(
        modifier = Modifier.width(80.dp),
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp,
            bottomEnd = 40.dp,
            bottomStart = 16.dp
        ),
        colors = CardDefaults.cardColors(colorResource(R.color.teal_100)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = time,
                style = MaterialTheme.typography.bodySmall,
                color = colorResource(R.color.dateColor),
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal
            )

            WeatherIcon(
                iconCode = icon,
                modifier = Modifier.size(40.dp)
            )

            Text(
                text = LocalizationHelper.convertToArabicNumbers(
                    "$temperature$temperatureUnit",
                    context
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal,
                color = colorResource(R.color.countryColor)
            )
        }
    }
}

