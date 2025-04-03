package com.example.forecanow.utils


import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult


class LocationManager(private val context: Context, private val fusedLocationProviderClient: FusedLocationProviderClient) {

    fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled(): Boolean {
        val systemLocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return systemLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                systemLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }


    @SuppressLint("MissingPermission")
    fun getFreshLocation(onSuccess: (Location) -> Unit, onFailure: (String) -> Unit) {
        fusedLocationProviderClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSuccess(location)
                } else {
                    requestNewLocation(onSuccess, onFailure)
                }
            }
            .addOnFailureListener { exception ->
                onFailure("Location error: ${exception.message}")
            }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocation(onSuccess: (Location) -> Unit, onFailure: (String) -> Unit) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 10000
            fastestInterval = 5000
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.let { result ->
                    val location = result.lastLocation
                    if (location != null) {
                        onSuccess(location)
                    } else {
                        onFailure("Failed to get location")
                    }
                } ?: onFailure("Location result is null")
            }

        }, Looper.getMainLooper())
    }

}