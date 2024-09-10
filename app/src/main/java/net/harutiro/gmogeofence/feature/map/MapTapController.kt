package net.harutiro.gmogeofence.feature.map

import android.graphics.Color
import net.harutiro.gmogeofence.model.LatLon
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

class MapTapController(private val mapView: MapView, private val getLocation: (LatLon) -> Unit) {

    init {
        val tapOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                if (p != null) {
                    clearMarkers()
                    placeMarker(p)
                    clearPolygon()
                    placePolygon(p)
                    updateLocationText(p)
                    getLocation(LatLon(p.latitude, p.longitude))
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean {
                return false
            }

            private fun clearMarkers() {
                val overlays = mapView.overlays
                for (i in overlays.size - 1 downTo 0) {
                    val overlay = overlays[i]
                    if (overlay is Marker) {
                        overlays.removeAt(i)
                    }
                }
            }

            private fun clearPolygon() {
                val overlays = mapView.overlays
                for (i in overlays.size - 1 downTo 0) {
                    val overlay = overlays[i]
                    if (overlay is Polygon) {
                        overlays.removeAt(i)
                    }
                }
            }

            private fun placeMarker(point: GeoPoint) {
                val marker = Marker(mapView)
                marker.position = point
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.add(marker)
                mapView.invalidate()
            }

            // 円形のポリゴンを描画する
            private fun placePolygon(point: GeoPoint) {

                val geoPointList = Polygon.pointsAsCircle(point, 300.0)
                val polygon = Polygon()
                polygon.points = geoPointList
                polygon.fillColor = Color.argb(75, 255, 0, 0)
                polygon.strokeColor = Color.argb(255, 255, 0, 0)
                polygon.strokeWidth = 2.0f

                mapView.overlays.add(polygon)
                mapView.invalidate()
            }

            private fun updateLocationText(point: GeoPoint) {
//                locationTextView.text = "緯度: ${point.latitude} 経度: ${point.longitude}"
            }
        })
        mapView.overlays.add(tapOverlay)
    }
}