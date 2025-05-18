package com.lames.standard.tools

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.provider.Settings
import androidx.core.content.getSystemService

fun Context.getBleAdapter(): BluetoothAdapter? {
    val adapter = (this.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    if (adapter == null) {
        LogKit.d(javaClass.simpleName, "无法获取蓝牙管理器")
    }
    return adapter
}


fun isBluetoothSupported(): Boolean = BluetoothAdapter.getDefaultAdapter() != null

fun isBluetoothTurnOn(): Boolean {
    val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter() ?: return false
    return bluetoothAdapter.isEnabled
}

fun isGpsOpen(context: Context): Boolean {
    val locationManager = context.getSystemService<LocationManager>() ?: return false
    val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    return gps || network
}

fun Activity.goToGpsSetting(): Boolean = try {
    val openGpsSetting = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    startActivityForResult(openGpsSetting, 0)
    true
} catch (e: Exception) {
    try {
        val openSetting = Intent(Settings.ACTION_SETTINGS)
        startActivityForResult(openSetting, 0)
        true
    } catch (e: Exception) {
        false
    }
}

fun getBlePermissions(): MutableList<String> {
    //return Permission.Group.BLUETOOTH.toMutableList()
    val permissions = arrayListOf(Manifest.permission.ACCESS_FINE_LOCATION)
    permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
    permissions.add(Manifest.permission.READ_PHONE_STATE)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        permissions.add(Manifest.permission.BLUETOOTH_SCAN)
        permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
        permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
    }
    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }*/
    return permissions
}