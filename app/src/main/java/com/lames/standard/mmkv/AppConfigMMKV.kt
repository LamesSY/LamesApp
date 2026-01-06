package com.lames.standard.mmkv

import com.lames.standard.App
import com.lames.standard.common.CommonApp
import com.lames.standard.common.GlobalVar
import com.lames.standard.entity.AppUpgradeInfo
import com.lames.standard.tools.androidId
import com.lames.standard.tools.parseToObject
import com.lames.standard.tools.versionCode

object AppConfigMMKV {

    private var newVersionInfo: String? by GlobalVar.obtain().appConfigMMKV.string("new_version_info_key", null)
    val newVersion get() = runCatching { parseToObject<AppUpgradeInfo>(newVersionInfo!!) }.getOrNull()
    suspend fun checkNewVersion() {

    }


    val isPreviewVersion get() = previewVersion.toLong() == CommonApp.obtain<App>().versionCode()
    var previewVersion: Int by GlobalVar.obtain().appConfigMMKV.int("preview_version_code")
    suspend fun getPreviewVersion() {

    }

    /**
     * 缓存的 androidId, 等同 Secure.getString($this$androidId.getContentResolver(), "android_id")，缓存起来，避免过多调用被应用市场针对
     */
    private var cacheAndroidId by GlobalVar.obtain().appConfigMMKV.string("cache_android_id", null)
    fun getAndroidId(): String {
        if (cacheAndroidId.isNullOrEmpty()) {
            //判断用户是否同意隐私协议
            if (agreePA) cacheAndroidId = CommonApp.obtain<App>().androidId()

        }
        return cacheAndroidId ?: ""
    }

    /**
     * 是否同意了用户协议 true是 false不同意
     */
    var agreePA: Boolean by GlobalVar.obtain().appConfigMMKV.boolean("agreePA", false)

    var adaptAppVersion: Long by GlobalVar.obtain().appConfigMMKV.long("app_version_0809", 1)

    var agreeDialog: Boolean by GlobalVar.obtain().appConfigMMKV.boolean("agreeDialog", false)

}