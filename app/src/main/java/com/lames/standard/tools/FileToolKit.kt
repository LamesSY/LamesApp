package com.lames.standard.tools

import android.content.Context
import android.os.Environment

/**
 * 获取缓存大小
 */
@Throws(Exception::class)
fun getTotalCacheSize(context: Context): String {
    var cacheSize = getFolderSize(context.cacheDir)
    val file = context.externalCacheDir
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && file != null) {
        cacheSize += getFolderSize(file)
    }
    return cacheSize.toFormattedFileSize()
}

/***
 * 清理所有缓存
 */
fun clearFileCache(context: Context) {
    deleteDir(context.cacheDir)
    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
        deleteDir(context.externalCacheDir)
    }
}
