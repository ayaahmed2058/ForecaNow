package com.example.forecanow.map

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.maps.model.LatLng
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@SuppressLint("ClickableViewAccessibility")
@Composable
fun OSMapView(
    modifier: Modifier = Modifier,
    onLocationSelected:  (LatLng) -> Unit = {}
) {
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                val mapController = this.controller
                mapController.setZoom(5.0)
                mapController.setCenter(GeoPoint(25.0, 35.0))

                this.setUseDataConnection(true)

                var currentMarker: Marker? = null

                this.setOnTouchListener { _, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        val geoPoint = this.projection.fromPixels(event.x.toInt(), event.y.toInt())
                        val latLng = LatLng(geoPoint.latitude, geoPoint.longitude)

                        currentMarker?.let { this.overlays.remove(it) }


                        val newMarker = Marker(this).apply {
                            position = geoPoint as GeoPoint?
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        }
                        this.overlays.add(newMarker)


                        currentMarker = newMarker

                        this.invalidate()


                        onLocationSelected(latLng)
                    }
                    false
                }
            }
        },
        update = { mapView ->
            mapView.invalidate()
        }
    )
}
