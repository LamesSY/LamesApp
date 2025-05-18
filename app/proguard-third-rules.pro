# ScanPlus
-ignorewarnings
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.huawei.hianalytics.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}

# Glide
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** { *; }

# MPAndroidChart
-dontwarn com.github.PhilJay.**
-keep class com.github.PhilJay.** { *; }

# MMKV
-dontwarn com.tencent.mmkv.**
-keep class com.tencent.mmkv.** { *; }

# BRV
-dontwarn com.github.liangjingkanji.**
-keep class com.github.liangjingkanji.** { *; }

# SmartRefreshLayout
-dontwarn com.scwang.smartrefresh.**
-keep class com.scwang.smartrefresh.** { *; }

# Gson
-dontwarn com.google.gson.**
-keep class com.google.gson.** { *; }

# Android-PickerView
-dontwarn com.contrarywind.**
-keep class com.contrarywind.** { *; }

# Flexbox
-dontwarn com.google.android.flexbox.**
-keep class com.google.android.flexbox.** { *; }

# CalendarView
-dontwarn com.haibin.**
-keep class com.haibin.** { *; }

# Tink
-dontwarn com.google.crypto.tink.**
-keep class com.google.crypto.tink.** { *; }

# BasePopup
-dontwarn razerdp.**
-keep class razerdp.** { *; }

# LoadSir
-dontwarn com.kingja.loadsir.**
-keep class com.kingja.loadsir.** { *; }

# PhotoView
-dontwarn com.github.chrisbanes.**
-keep class com.github.chrisbanes.** { *; }

# GlideImageLoader
-dontwarn com.github.piasy.**
-keep class com.github.piasy.** { *; }

# Android Image Cropper
-dontwarn com.vanniktech.**
-keep class com.vanniktech.** { *; }

# Aliyun OSS
-dontwarn com.aliyun.dpa.**
-keep class com.aliyun.dpa.** { *; }

# XXPermissions
-dontwarn com.hjq.permissions.**
-keep class com.hjq.permissions.** { *; }

# ZXing
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** { *; }

# ZXing Android Embedded
-dontwarn com.journeyapps.**
-keep class com.journeyapps.** { *; }

# CameraX
-dontwarn androidx.camera.**
-keep class androidx.camera.** { *; }

# 百度语音识别
-keep class com.baidu.speech.**{*;}

# 阿里push
-keepclasseswithmembernames class ** {
    native <methods>;
}

# 云康宝体脂秤
-keep class com.qingniu.scale.model.BleScaleData{*;}

-keepattributes Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.taobao.** {*;}
-keep class com.alibaba.** {*;}
-keep class com.alipay.** {*;}
-keep class com.ut.** {*;}
-keep class com.ta.** {*;}
-keep class anet.**{*;}
-keep class anetwork.**{*;}
-keep class org.android.spdy.**{*;}
-keep class org.android.agoo.**{*;}
-keep class android.os.**{*;}
-keep class org.json.**{*;}
-dontwarn com.taobao.**
-dontwarn com.alibaba.**
-dontwarn com.alipay.**
-dontwarn anet.**
-dontwarn org.android.spdy.**
-dontwarn org.android.agoo.**
-dontwarn anetwork.**
-dontwarn com.ut.**
-dontwarn com.ta.**
-dontwarn com.jieli.**
-dontwarn no.nordicsemi.**