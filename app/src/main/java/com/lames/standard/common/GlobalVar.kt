package com.lames.standard.common

import android.os.Handler
import android.os.Looper
import com.tencent.mmkv.MMKV
import com.lames.standard.App
import com.lames.standard.tools.getScreenHeight
import com.lames.standard.tools.getScreenWidth
import com.lames.standard.tools.isExternalStorageAvailable
import java.io.File

/**
 * Created by Jason
 */
class GlobalVar private constructor() {

    /**
     * 屏幕宽度
     */
    val screenWith: Int by lazy { CommonApp.obtain<App>().getScreenWidth() }

    /**
     * 屏幕高度
     */
    val screenHeight: Int by lazy { CommonApp.obtain<App>().getScreenHeight() }

    /**
     * 应用沙盒文件根目录
     */
    val appRootPath: String

    /**
     * 图片缓存目录
     */
    val imgCachePath: String

    /**
     * 用户相关的 mmkv
     */
    val userMMKV: MMKV

    /**
     * app配置相关 mmkv
     */
    val appConfigMMKV: MMKV

    /**
     * 设备相关的 mmkv
     */
    val deviceMMKV: MMKV


    init {
        val app = CommonApp.obtain<App>()
        if (isExternalStorageAvailable()) {
            appRootPath = app.getExternalFilesDir(null)?.absolutePath ?: app.filesDir.absolutePath
            imgCachePath = app.externalCacheDir?.absolutePath ?: app.cacheDir.absolutePath
        } else {
            appRootPath = app.filesDir.absolutePath
            imgCachePath = app.cacheDir.absolutePath
        }
        MMKV.initialize(app)
        userMMKV = MMKV.defaultMMKV()
        appConfigMMKV = MMKV.mmkvWithID("app_config")
        deviceMMKV = MMKV.mmkvWithID("device_config")
    }

    /**
     * webview缓存文件路径
     */
    val webViewPath: String by lazy {
        val path = "$appRootPath${File.separator}webViewCache${File.separator}"
        File(path).apply { if (exists().not()) mkdirs() }
        return@lazy path
    }

    /**
     * 文件下载路径
     */
    val downloadPath: String by lazy {
        val path = "$appRootPath${File.separator}${Constants.Folder.DOWNLOAD}${File.separator}"
        File(path).apply { if (exists().not()) mkdirs() }
        return@lazy path
    }

    /**
     * 图片保存路径
     */
    val imagePath: String by lazy {
        val path = "$appRootPath${File.separator}${Constants.Folder.IMAGE}${File.separator}"
        File(path).apply { if (exists().not()) mkdirs() }
        return@lazy path
    }

    /**
     * 视频保存路径
     */
    val videoPath: String by lazy {
        val path = "$appRootPath${File.separator}${Constants.Folder.VIDEO}${File.separator}"
        File(path).apply { if (exists().not()) mkdirs() }
        return@lazy path
    }

    /**
     * 音频保存路径
     */
    val audioPath: String by lazy {
        val path = "$appRootPath${File.separator}${Constants.Folder.AUDIO}${File.separator}"
        File(path).apply { if (exists().not()) mkdirs() }
        return@lazy path
    }

    companion object {
        private object Holder {
            val instance = GlobalVar()
        }

        val globalMainHandler = Handler(Looper.getMainLooper())
        fun obtain(): GlobalVar = Holder.instance
    }
}