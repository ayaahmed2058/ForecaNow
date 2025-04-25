package com.example.forecanow.pojo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LocationData(
    val lat: Double,
    val lon: Double,
    val name: String
) : Parcelable