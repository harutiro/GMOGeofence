package net.harutiro.gmogeofence.feature.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import net.harutiro.gmogeofence.feature.notification.GeoNotification

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    val TAG = "GeofenceBroadcastReceiver"
    // ...
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG,"Geofenceのイベントが走った")

        // GeoFencingイベントを取得
        val geofencingEvent = GeofencingEvent.fromIntent(intent!!)

        // エラーハンドリング
        if (geofencingEvent?.hasError() == true) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e(TAG, errorMessage)
            return
        }

        // GeoFencingイベントのタイプを取得
        val geofenceTransition = geofencingEvent?.geofenceTransition

        // 入室・退室・滞在しているかの判定
        if (
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ||
            geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL
        ) {
            // ジオフェンスのトリガーイベントが発生した場合の処理
            val triggeringGeofence: List<Geofence>? = geofencingEvent.triggeringGeofences

            // トリガーされたジオフェンスの情報を利用して必要な処理を行います
            if (triggeringGeofence != null) {
                for (geofence in triggeringGeofence) {
                    val requestId: String = geofence.requestId
                    // ジオフェンスの情報を利用して必要な処理を行います
                    // 例: ジオフェンスのIDを表示
                    Log.d(TAG, "Geofence ID: $requestId")
                    // どの状態でいるか
                    Log.d(TAG, "Geofence Transition: ${geofence.transitionTypes}")

                    sendNotification(context, "ジオフェンスの「${transitionTypes(geofence.transitionTypes)}」ました")
                }
            }
        }
    }

    private fun transitionTypes(transitionTypes: Int): String {
        if(transitionTypes == Geofence.GEOFENCE_TRANSITION_ENTER){
            return "エリアの中に入った"
        }else if(transitionTypes == Geofence.GEOFENCE_TRANSITION_EXIT){
            return "エリアから出た"
        }else if(transitionTypes == Geofence.GEOFENCE_TRANSITION_DWELL){
            return "エリアに滞在中です"
        }else{
            return "その他"
        }
    }

    private fun sendNotification(context: Context?, message: String) {
        // 通知の発行
        if(context != null){
            val geoNotification = GeoNotification()
            geoNotification.showNotification(context,message)
        }
    }
}