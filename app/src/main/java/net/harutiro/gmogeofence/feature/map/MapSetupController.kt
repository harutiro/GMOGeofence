package net.harutiro.gmogeofence.feature.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class MapSetupController(private val context: Context, private val mapView: MapView) {

    private val TAG = "MapSetupController"
    private val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)

    fun setupMapWithLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val centerPoint = GeoPoint(location.latitude, location.longitude)

                    mapView.setTileSource(TileSourceFactory.MAPNIK)
                    val mapController: IMapController? = mapView.controller
                    mapController?.setZoom(17)
                    mapController?.setCenter(centerPoint)

                    val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
                    locationOverlay.enableMyLocation()
                    mapView.overlays.add(locationOverlay)

                    val compassOverlay = CompassOverlay(context, InternalCompassOrientationProvider(context), mapView)
                    compassOverlay.enableCompass()
                    mapView.overlays.add(compassOverlay)
                } else {
                    Log.d(TAG, "Location not available")
                }
            }
    }
}