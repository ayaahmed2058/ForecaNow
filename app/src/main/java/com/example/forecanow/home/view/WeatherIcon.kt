package com.example.forecanow.home.view

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.forecanow.R
import com.example.forecanow.utils.MapImage.Companion.getWeatherIconRes


@Composable
fun WeatherIcon(
    iconCode: String,
    modifier: Modifier = Modifier
) {
    val iconRes = remember(iconCode) { getWeatherIconRes(iconCode) }

    Icon(
        painter = painterResource(id = iconRes),
        contentDescription = stringResource(R.string.weather_icon),
        modifier = modifier,
        tint = Color.Unspecified
    )
}