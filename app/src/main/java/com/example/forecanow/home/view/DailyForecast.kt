package com.example.forecanow.home.view

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.forecanow.utils.Format.formatDate
import com.example.forecanow.utils.LocalizationHelper
import com.example.forecanow.utils.checkdays.isSameDay
import com.example.forecanow.utils.customFontFamily
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun DailyForecastItem(item: HourlyWeather, temperatureUnit: String) {
    val context = LocalContext.current
    val date = formatDate(item.dt, context)
    val temp = item.main.temp.toInt()
    val icon = item.weather.firstOrNull()?.icon ?: "01d"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(colorResource(R.color.teal_200)),

        ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date,
                style = MaterialTheme.typography.bodyLarge,
                color = colorResource(R.color.white),
                modifier = Modifier.weight(1f),
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal
            )

            WeatherIcon(
                iconCode = icon,
                modifier = Modifier.size(65.dp)
            )

            Text(
                text = LocalizationHelper.convertToArabicNumbers("$temp$temperatureUnit", context),
                style = MaterialTheme.typography.bodyMedium,
                color = colorResource(R.color.countryColor),
                fontFamily = customFontFamily,
                fontWeight = FontWeight.Normal
            )
        }
    }
}



fun extractDailyForecast(hourlyList: List<HourlyWeather>, context: Context): List<HourlyWeather> {
    val calendar = Calendar.getInstance()
    val currentDate = calendar.time

    return hourlyList
        .filter { item ->
            val itemDate = Date(item.dt * 1000)
            !isSameDay(currentDate, itemDate)
        }
        .groupBy { item ->
            if (LocalizationHelper.isArabicLanguage(context)) {
                SimpleDateFormat("yyyy-MM-dd", Locale("ar"))
                    .format(Date(item.dt * 1000))
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(Date(item.dt * 1000))
            }
        }
        .map { (_, items) ->
            items.maxByOrNull { it.main.temp } ?: items.first()
        }
        .sortedBy { it.dt }
        .take(5)
}


