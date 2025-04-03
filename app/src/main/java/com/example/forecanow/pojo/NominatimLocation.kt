package com.example.forecanow.pojo

import com.google.gson.annotations.SerializedName

data class NominatimLocation(
    @SerializedName("display_name") val displayName: String,
    @SerializedName("lat") val lat: String,
    @SerializedName("lon") val lon: String
)