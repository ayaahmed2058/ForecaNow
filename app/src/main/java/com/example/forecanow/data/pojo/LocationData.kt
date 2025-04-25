package com.example.forecanow.data.pojo
import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class LocationData(
    val lat: Double,
    val lon: Double,
    val name: String
) : Parcelable