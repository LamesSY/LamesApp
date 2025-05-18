package com.lames.standard.mmkv

import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonApp
import com.lames.standard.common.GlobalVar
import com.lames.standard.entity.AppUpgradeInfo
import com.lames.standard.network.getRequest
import com.lames.standard.tools.androidId
import com.lames.standard.tools.parseToJson
import com.lames.standard.tools.parseToObject
import com.lames.standard.tools.versionCode
import rxhttp.tryAwait

object AppConfigMMKV {

    var textScaleType: Int by GlobalVar.obtain().appConfigMMKV.int("app_text_scale_type", 1)

    val appThemeResId
        get() = when (textScaleType) {
            2 -> R.style.AppThemeScale1
            3 -> R.style.AppThemeScale2
            else -> R.style.AppTheme
        }

    val webViewTextZoom
        get() = when (textScaleType) {
            2 -> 110
            3 -> 125
            else -> 100
        }

    private var newVersionInfo: String? by GlobalVar.obtain().appConfigMMKV.string(
        "new_version_info_key",
        null
    )
    var newVersion: AppUpgradeInfo? = null
        get() = if (field == null) try {
            field = newVersionInfo?.let { parseToObject<AppUpgradeInfo>(it) }
            field
        } catch (e: Exception) {
            null
        } else field
        set(value) {
            newVersionInfo = value?.let { parseToJson(it) }
            field = value
        }


    val isPreviewVersion get() = previewVersion.toLong() == CommonApp.obtain<App>().versionCode()
    var previewVersion: Int by GlobalVar.obtain().appConfigMMKV.int("preview_version_code")
    suspend fun getPreviewVersion() {

    }

    /**
     * 检查更新版本
     */
    suspend fun checkNewVersion() {

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

    /**
     * 当前是否打开通知开关
     */
    var notificationToggle: Boolean by GlobalVar.obtain().appConfigMMKV.boolean(
        "NOTIFICATION_TOGGLE",
        true
    )


    /**
     * 用于记录之前是否已授权，如果在查询状态变为未授权时，可以根据之前这个值来进行判断，弹窗提示授权失效
     */
    var lastAuthorizeHealthKitResult: Boolean by GlobalVar.obtain().appConfigMMKV.boolean(
        "LAST_AUTHORIZE_HEALTH_KIT_RESULT",
        false
    )
}