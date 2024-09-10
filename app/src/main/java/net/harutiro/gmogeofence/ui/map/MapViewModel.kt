package net.harutiro.gmogeofence.ui.map

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.harutiro.gmogeofence.feature.geofence.GeofenceRepository
import net.harutiro.gmogeofence.model.EntryData
import net.harutiro.gmogeofence.model.LatLon

// ここで取得できる
class MainViewModel(_app: Application): AndroidViewModel(_app) {
    val TAG = "MainViewModel"

    private val applicationContext: Context = _app.applicationContext

    private var currentLatLng: LatLon = LatLon(0.0, 0.0) // 初期値は適当な値で
    private val _latlng = MutableStateFlow(LatLon(0.0,0.0))
    val latlng = _latlng.asStateFlow()

    var geofenceRepository = GeofenceRepository(applicationContext)

    // MainViewModel内で、UIを更新しないようにする関数を用意する
    fun setLatLngWithoutTriggeringUI(newLatLng: LatLon) {
        if (newLatLng != currentLatLng) {
            currentLatLng = newLatLng
            // UIを更新するStateを変更せずに、データのみを更新する
            _latlng.value = newLatLng
        }
    }

    fun startGeofence() {
        Log.d(TAG,"startGeofence")

        // application から activityを受け取る

        val entry = EntryData(
            key = "test",
            value = latlng.value
        )

        val radius = 300f

        geofenceRepository.createGeofence(entry,radius)

        geofenceRepository.addGeoFences()

    }


    fun stopGeofence() {
        Log.d(TAG,"stopGeofence")
        geofenceRepository.stopGeoFence()
    }


}