package com.example.forecanow

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.forecanow.utils.customFontFamily
import kotlinx.coroutines.delay

class WelcomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPreferences = getSharedPreferences("weather_preferences", Context.MODE_PRIVATE)
        val isFirstLaunch = sharedPreferences.getBoolean("isFirstTime", true)

        if (isFirstLaunch) {

            setContent {
                WelcomeScreenUI(sharedPreferences)
            }
        } else {

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}




@SuppressLint("SuspiciousIndentation")
@Composable
fun WelcomeScreenUI(sharedPreferences: SharedPreferences) {
    val context = LocalContext.current
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.weather_anim))


    LaunchedEffect(Unit) {
        delay(2000)
        sharedPreferences.edit().putBoolean("isFirstTime", false).apply()
        context.startActivity(Intent(context, MainActivity::class.java))
        if (context is ComponentActivity) {
            context.finish()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.teal_700)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LottieAnimation(
            composition,
            iterations = LottieConstants.IterateForever,
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(
            text = stringResource(R.string.app_name),
            fontSize = 32.sp,
            fontFamily = customFontFamily,
            fontWeight = FontWeight.ExtraLight,
            color = colorResource(id = R.color.teal_100)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.stay_updated_with_the_latest_weather_conditions_get_forecasts_for_your_location_or_search_any_city_worldwide),
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}
