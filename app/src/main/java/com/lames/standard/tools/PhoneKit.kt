@file:JvmName("PhoneKit")

package com.lames.standard.tools

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi

private var androidId: String = ""

fun Context.androidId(): String {
    if (androidId.isEmpty()) androidId =
        Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    return androidId
}

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun supportX86(): Boolean = Build.SUPPORTED_ABIS?.contains("x86") ?: false

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun supportArmeabi(): Boolean = Build.SUPPORTED_ABIS?.contains("armeabi") ?: false

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun supportArmeabiV7a(): Boolean = Build.SUPPORTED_ABIS?.contains("armeabi-v7a") ?: false

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
fun supportArm64V8a(): Boolean = Build.SUPPORTED_ABIS?.contains("arm64-v8a") ?: false