package com.islabuy.camiguin.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

@Composable
fun PickupLocationSelector(
    initialLat: Double? = null,
    initialLng: Double? = null,
    onLocationSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    val start = if (initialLat != null && initialLng != null)
                        GeoPoint(initialLat, initialLng) else GeoPoint(9.1731, 124.7284) // Camiguin center
                    controller.setCenter(start)
                    mapView = this

                    // Tap to place/move pin
                    setOnTouchListener { _, event ->
                        if (event.action == android.view.MotionEvent.ACTION_UP) {
                            val gp = projection.fromPixels(event.x.toInt(), event.y.toInt()) as? GeoPoint
                            gp?.let {
                                selectedPoint = it
                                overlays.clear()
                                val marker = Marker(this).apply {
                                    position = it
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Pickup Location"
                                }
                                overlays.add(marker)
                                invalidate()
                                onLocationSelected(it.latitude, it.longitude)
                            }
                        }
                        false
                    }
                }
            },
            modifier = Modifier.weight(1f)
        )

        // OSM Attribution (required)
        Text(
            text = "© OpenStreetMap contributors",
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(8.dp)
        )

        selectedPoint?.let { point ->
            Text(
                text = "Lat: ${point.latitude}, Lng: ${point.longitude}",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

/**
 * Open external navigation app with seller coordinates.
 * Supports Google Maps, Waze, and other compatible apps.
 */
fun openExternalNavigation(context: android.content.Context, lat: Double, lng: Double) {
    // Try Google Maps first
    val gmmIntentUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng")
    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
        setPackage("com.google.android.apps.maps")
    }
    if (mapIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(mapIntent)
        return
    }

    // Try Waze
    val wazeUri = Uri.parse("https://waze.com/ul?ll=$lat,$lng&navigate=yes")
    val wazeIntent = Intent(Intent.ACTION_VIEW, wazeUri)
    if (wazeIntent.resolveActivity(context.packageManager) != null) {
        context.startActivity(wazeIntent)
        return
    }

    // Fallback: any app that handles geo: URIs
    val fallbackIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng"))
    context.startActivity(fallbackIntent)
}
