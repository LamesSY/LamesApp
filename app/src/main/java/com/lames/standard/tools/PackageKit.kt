@file:JvmName("PackageKit")

package com.lames.standard.tools

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.getSystemService
import com.lames.standard.BuildConfig

/**
 * Created by Jason
 */
val Context.currentProcessName: String
    get() {
        val systemService = getSystemService<ActivityManager>()
        systemService?.runningAppProcesses?.forEach { info ->
            if (info.pid == android.os.Process.myPid()) return info.processName
        }
        return ""
    }

fun Context.versionCode(): Long = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    packageManager?.getPackageInfo(packageName, 0)?.longVersionCode ?: -1L
} else {
    packageManager?.getPackageInfo(packageName, 0)?.versionCode?.toLong() ?: -1L
}

fun Context.getPackageInfo(packageName: String): PackageInfo? = try {
    packageManager?.getPackageInfo(packageName, PackageManager.GET_META_DATA)
} catch (e: PackageManager.NameNotFoundException) {
    null
}

fun Context.getAppInfo(pkgName: String = packageName): ApplicationInfo? = try {
    packageManager?.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
} catch (e: PackageManager.NameNotFoundException) {
    null
}

fun Context.versionName(): String? = packageManager?.getPackageInfo(packageName, 0)?.versionName

fun <T> Context.getMetaData(key: String): T? = getAppInfo()?.metaData?.get(key) as? T

fun appVersion(): String = BuildConfig.VERSION_NAME + ".${BuildConfig.VERSION_CODE}"