package com.example.forecanow

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class WelcomeScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WelcomeScreenUI()
        }
    }
}

@SuppressLint("SuspiciousIndentation")
@Preview(showSystemUi = true , showBackground = true)
@Composable
fun WelcomeScreenUI() {

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.purple_30)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = R.drawable.weather_icon),
            contentDescription = "Weather Icon",
        )

        Spacer(modifier = Modifier.height(36.dp))

        Text(text = "ForecaNow", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = colorResource(id = R.color.purple_500))

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Stay updated with the latest weather conditions. Get forecasts for your location or search any city worldwide!",
            fontSize = 16.sp,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(60.dp))
        Button(
            onClick = {
                val intent = Intent (context , MainActivity::class.java)
                context.startActivity(intent)
            },
            shape = RoundedCornerShape(50),
            modifier = Modifier
                .shadow(.6.dp, RoundedCornerShape(50))
                .background(colorResource(id = R.color.purple_50)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Text(text = "Get Started", color = Color.White)
        }
    }

}
