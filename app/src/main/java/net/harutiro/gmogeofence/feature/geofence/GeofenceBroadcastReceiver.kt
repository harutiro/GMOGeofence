package net.harutiro.gmogeofence.feature.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    val TAG = "GeofenceBroadcastReceiver"
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d(TAG,"onReceive")

        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)


        Log.d(TAG,"intent: $intent")
        Log.d(TAG,"geofencingEvent: $geofencingEvent")

        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // Test that the reported transition was of interest.
        if (
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT
        ) {

            val triggeringGeofences = geofencingEvent.triggeringGeofences

            if (geofencingEvent.hasError()) {
                // エラーイベントが発生した場合の処理
                val errorCode: Int = geofencingEvent.errorCode
                Log.e("Geofence", "Geofencing error: $errorCode")
            } else {
                // ジオフェンスのトリガーイベントが発生した場合の処理
                val triggeringGeofence: List<Geofence>? = geofencingEvent.triggeringGeofences

                // トリガーされたジオフェンスの情報を利用して必要な処理を行います
                if (triggeringGeofence != null) {
                    for (geofence in triggeringGeofence) {
                        val requestId: String = geofence.requestId
                        // ジオフェンスの情報を利用して必要な処理を行います
                        // 例: ジオフェンスのIDを表示
                        Log.d(TAG, "Geofence ID: $requestId")
                    }
                }
            }

//            // Get the transition details as a String.
//            val geofenceTransitionDetails = getGeofenceTransitionDetails(
//                this,
//                geofenceTransition,
//                triggeringGeofences
//            )
//
//            // Send notification and log the transition details.
//            sendNotification(geofenceTransitionDetails)
            Log.i(TAG, "とりあえずジオフェンスのイベントが走った" + triggeringGeofences.toString())
        } else {
            // Log the error
            Log.e(TAG, "geofence error")
        }
    }
}