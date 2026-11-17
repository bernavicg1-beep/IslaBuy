package com.islabuy.camiguin.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.res.stringResource
import com.islabuy.camiguin.R
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import com.islabuy.camiguin.ui.components.ThreeDButton

// Camiguin island centre and a zoom that frames the whole island (~24 x 6 km).
private const val CAMIGUIN_LAT = 9.1731
private const val CAMIGUIN_LNG = 124.7284
private const val ISLAND_ZOOM = 11.0
private const val PIN_ZOOM = 15.0

@Composable
fun PickupLocationSelector(
    initialLat: Double? = null,
    initialLng: Double? = null,
    onLocationSelected: (Double, Double) -> Unit
) {
    val context = LocalContext.current
    var mapView by remember { mutableStateOf<MapView?>(null) }
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    var myLocationOverlay by remember { mutableStateOf<MyLocationNewOverlay?>(null) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
        val mv = mapView
        val overlay = myLocationOverlay
        if (granted && mv != null && overlay != null) {
            overlay.enableMyLocation()
            overlay.runOnFirstFix {
                mv.controller.animateTo(overlay.myLocation)
            }
        }
    }

    LaunchedEffect(Unit) {
        Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", 0))
    }

    Column(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)

                    // Show the whole island first; never plant a fake pin in the middle.
                    controller.setZoom(ISLAND_ZOOM)
                    controller.setCenter(GeoPoint(CAMIGUIN_LAT, CAMIGUIN_LNG))
                    mapView = this

                    val overlay = MyLocationNewOverlay(GpsMyLocationProvider(ctx), this)
                    myLocationOverlay = overlay
                    if (hasLocationPermission) {
                        overlay.enableMyLocation()
                        overlay.runOnFirstFix { controller.animateTo(overlay.myLocation) }
                    }
                    overlays.add(overlay)

                    // Existing pin (editing / viewing a seller's saved location).
                    if (initialLat != null && initialLng != null) {
                        val pt = GeoPoint(initialLat, initialLng)
                        selectedPoint = pt
                        overlays.add(Marker(this).apply {
                            position = pt
                            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                            title = "Pickup Location"
                        })
                        controller.setZoom(PIN_ZOOM)
                        controller.setCenter(pt)
                    }

                    // Tap to place / move the pin.
                    setOnTouchListener { view, event ->
                        if (event.action == android.view.MotionEvent.ACTION_UP) {
                            val gp = projection.fromPixels(event.x.toInt(), event.y.toInt()) as? GeoPoint
                            gp?.let {
                                selectedPoint = it
                                // Keep the my-location overlay; drop only markers.
                                overlays.removeAll { o -> o is Marker }
                                overlays.add(Marker(this).apply {
                                    position = it
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Pickup Location"
                                })
                                invalidate()
                                onLocationSelected(it.latitude, it.longitude)
                            }
                        }
                        if (event.action == android.view.MotionEvent.ACTION_UP) {
                            view.performClick()
                        }
                        false
                    }
                }
            },
            onRelease = { mv ->
                myLocationOverlay?.disableMyLocation()
                mv.onDetach()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // Controls row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            com.islabuy.camiguin.ui.components.ThreeDButton(
                text = if (hasLocationPermission) "My location" else "Enable location",
                onClick = {
                    if (hasLocationPermission) {
                        myLocationOverlay?.enableMyLocation()
                        myLocationOverlay?.runOnFirstFix {
                            mapView?.controller?.animateTo(myLocationOverlay?.myLocation)
                        }
                    } else {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                },
                modifier = Modifier
            )
            ThreeDButton(
                text = "Whole island",
                onClick = {
                    mapView?.controller?.setZoom(ISLAND_ZOOM)
                    mapView?.controller?.setCenter(GeoPoint(CAMIGUIN_LAT, CAMIGUIN_LNG))
                },
                modifier = Modifier
            )
        }

        // OSM Attribution (required)
        Text(
            text = stringResource(R.string.osm_attribution),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
        )

        selectedPoint?.let { point ->
            Text(
                text = "Lat: ${"%.5f".format(point.latitude)}, Lng: ${"%.5f".format(point.longitude)}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
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
