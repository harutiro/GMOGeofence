package net.harutiro.gmogeofence.feature.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import net.harutiro.gmogeofence.model.EntryData

class GeofenceRepository(_activity: Context) {

    val TAG = "GeofenceRepository"

    var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(_activity)

    private val geofenceList = mutableListOf<Geofence>()
    private val activity = _activity

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(activity, GeofenceBroadcastReceiver::class.java)
        // FLAG_UPDATE_CURRENT を使用して、呼び出し時に同じ保留中のインテントを取得します。
        // addGeofences() and removeGeofences().
        // PendingIntentはMUTABLEでなければならない　Intentで渡すデータが変わるから
        PendingIntent.getBroadcast(activity, 0, intent, PendingIntent.FLAG_MUTABLE)
    }

    fun createGeofence(entry: EntryData, radius: Float) {
        val item = Geofence.Builder()
            // ジオフェンスを識別するためのID
            .setRequestId(entry.key)

            // ジオフェンスの範囲と場所を設定
            .setCircularRegion(
                entry.value.latitude,
                entry.value.longitude,
                radius
            )

            // ジオフェンスの有効期限を設定　今回は無期限
            .setExpirationDuration(Geofence.NEVER_EXPIRE)

            // 対象となる遷移タイプを設定します。 アラートはこれらに対してのみ生成されます
            // このサンプルでは、入口と出口の遷移を追跡します。
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)

            // 通知の初期遅延を設定します。 このサンプルでは、ジオフェンスに入るとすぐにアラートが生成されます。
//            .setNotificationResponsiveness(5000)

            // ジオフェンスを作成します。
            .build()

        geofenceList.add(item)
    }

    fun getGeofencingRequest(): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            // トリガーを設定します。 このサンプルでは、ジオフェンスに入るとすぐにアラートが生成されます。
            //Geofence.GEOFENCE_TRANSITION_ENTERジオフェンスが追加された時点で、デバイスがすでにそのジオフェンス内にある場合に、ジオフェンス サービスが通知をトリガーする必要があることを示すフラグ。
            //DWELL が　一番電池持ちの観点でよき　100m にするともっと電池持ちがいい
            //Geofence.GEOFENCE_TRANSITION_DWELLジオフェンスが追加されたとき、およびデバイスがしばらくの間すでにそのジオフェンス内にある場合に、ジオフェンス サービスが通知をトリガーする必要があることを示すフラグ。
            //Geofence.GEOFENCE_TRANSITION_EXITジオフェンスが追加された時点で、デバイスがすでにそのジオフェンスの外にある場合に、ジオフェンス サービスが通知をトリガーする必要があることを示すフラグ。
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)

            // ジオフェンスのリストを追加します。
            addGeofences(geofenceList)
        }.build()
    }

    fun addGeoFences() {
        if (ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            geofencingClient.addGeofences(getGeofencingRequest(), geofencePendingIntent).run {
                addOnSuccessListener {
                    // Geofences added
                    // ...
                    Log.d(TAG, "addGeofences: Success")
                }
                addOnFailureListener {
                    // Failed to add geofences
                    // ...
                    Log.e(TAG, "addGeofences: Failure")
                    Log.e(TAG, it.toString())
                }
            }
        }

    }

    fun stopGeoFence() {
        geofencingClient.removeGeofences(geofencePendingIntent).run {
            addOnSuccessListener {
                // Geofences removed
                // ...
                Log.d(TAG, "stopGeofece: Success")
            }
            addOnFailureListener {
                // Failed to remove geofences
                // ...
                Log.e(TAG, "stopGeofece: Failure")
            }
        }
    }
}