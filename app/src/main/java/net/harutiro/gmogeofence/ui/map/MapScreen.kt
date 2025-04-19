package net.harutiro.gmogeofence.ui.map

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import net.harutiro.gmogeofence.model.LatLon
import net.harutiro.gmogeofence.ui.theme.GMOGeofenceTheme
import net.harutiro.gmogeofence.feature.map.MapSetupController
import net.harutiro.gmogeofence.feature.map.MapTapController
import net.harutiro.gmogeofence.feature.notification.GeoNotification
import net.harutiro.gmogeofence.ui.map.component.MapView
import net.harutiro.gmogeofence.utils.EnergyCallback
import net.harutiro.gmogeofence.utils.EnergyChecker
import net.harutiro.gmogeofence.utils.OtherFileStorage

@Composable
fun MapScreen(mainViewModel:MainViewModel = viewModel()){
    val context = LocalContext.current
    val activity: Activity = LocalContext.current as Activity

    var energyValue by remember { mutableLongStateOf(0L) }

    // csvファイル書き込み
    var otherFileStorage: OtherFileStorage? = null

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // 許可された
        }
    }

    Box {
        MapView { map ->
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100)
            } else {
                // 位置情報を取得する
                MapSetupController(context, map).setupMapWithLocation()

                // マップタップイベントを設定
                MapTapController(map) { tappedLatLng ->
                    // UIを更新せずにViewModelのデータを更新する
                    mainViewModel.setLatLngWithoutTriggeringUI(tappedLatLng)
                }
            }
        }

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 16.dp,
                    vertical = 16.dp
                )//右下に配置
                .align(Alignment.TopCenter),

            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Button(onClick = {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) != PackageManager.PERMISSION_GRANTED
                ){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }){
                Text("通知パーミッション許可")
            }
            Button(
                onClick = {
                    mainViewModel.startGeofence()
                }
            ) {
                Text("Start Geofence")
            }

            Button(
                onClick = {
                    mainViewModel.stopGeofence()
                }
            ) {
                Text("止める")
            }


            Button(
                onClick = {
                    otherFileStorage = OtherFileStorage(context)
                    val energyChecker = EnergyChecker(context)
                    // エネルギーチェックを開始
                    energyChecker.start(object : EnergyCallback {
                        override fun onEnergyChecked(energy: Long) {
                            // ここでエネルギー値を処理する
                            energyValue = energy
                            otherFileStorage?.doLog(energy.toString())
                        }
                    })
                }
            ) {
                Text(text = "電量を確認する")
            }

            Button(
                onClick = {
                    otherFileStorage = null
                    val energyChecker = EnergyChecker(context)
                    // エネルギーチェックを停止
                    energyChecker.stop()
                }
            ) {
                Text(text = "電量チェックを止める")
            }

            Text(text = "電量: $energyValue μAh")
        }

    }
}





@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GMOGeofenceTheme {
        MapScreen()
    }
}