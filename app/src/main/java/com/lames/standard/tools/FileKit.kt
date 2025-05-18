@file:JvmName("FileKit")

package com.lames.standard.tools

import android.content.Context
import android.content.res.AssetManager
import android.net.Uri
import android.os.Environment
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.text.DecimalFormat

/**
 * Created by Jason
 */
val Long.toB: String get() = DecimalFormat("0.##B").format(this.toFloat())
val Long.toKB: String get() = DecimalFormat("0.##KB").format(this / 1024.0)
val Long.toMB: String get() = DecimalFormat("0.##MB").format(this / 1048576.0)
val Long.toGB: String get() = DecimalFormat("0.##GB").format(this / 1073741824.0)

/**
 * 格式化 Long 型的文件大小到 B, KB, MB, GB
 */
fun Long.toFormattedFileSize() = when (this) {
    in 0 until 1024 -> toB
    in 1024 until 1048576 -> toKB
    in 1048576 until 1073741824 -> toMB
    else -> toGB
}

/**
 * 获取文件格式后缀如png, pdf，不带.
 */
val File.format: String get() = name.substringAfterLast(".", "")

/**
 * 外置存储是否可读
 */
fun isExternalStorageAvailable(): Boolean =
    Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED

/**
 * 将流写入文件并返回文件路径
 */
@Throws(IOException::class)
fun InputStream.toFile(file: File?): String? {
    if (file == null) return null
    val os = FileOutputStream(file)
    try {
        var bytesRead = 0
        val buffer = ByteArray(8192)
        while (run {
                bytesRead = read(buffer, 0, 8192)
                bytesRead
            } != -1) {
            os.write(buffer, 0, bytesRead)
        }
    } finally {
        try {
            os.flush()
            os.close()
            close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return file.absolutePath
}

/**
 * 将文本写入文件并返回文件路径
 */
@Throws(IOException::class)
fun String.toFile(file: File?): String? {
    if (file == null) return null
    val os = FileOutputStream(file)
    try {
        os.write(toByteArray())
    } finally {
        try {
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return file.absolutePath
}

/**
 * 阻塞当前线程方法，拷贝Uri所指向的文件到目标目录
 * @return 返回文件路径
 */
@Throws(Exception::class)
fun copyFileFromUri(context: Context, uri: Uri, destFile: File): String? {
    val inputStream = context.contentResolver.openInputStream(uri)
    return inputStream?.toFile(destFile)
}

/**
 * 获取文件大小
 */
@Throws(Exception::class)
fun getFolderSize(file: File): Long {
    var size: Long = 0
    try {
        val fileList = file.listFiles() ?: return size
        fileList.forEach {
            size = if (it.isDirectory) size + getFolderSize(it) else size + it.length()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return size
}

/**
 * 删除目录
 */
fun deleteDir(dir: File?) {
    if (dir != null && dir.isDirectory) {
        val children = dir.listFiles()
        children?.forEach { deleteDir(it) }
    }
    dir?.delete()
}


/**
 * 从assets中读取文本数据
 */
fun readTextFileFromAssets(context: Context, fileName: String): String {
    val assetManager: AssetManager = context.assets
    val stringBuilder = StringBuilder()
    assetManager.open(fileName).use { inputStream ->
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var line = reader.readLine()
            while (line != null) {
                stringBuilder.append(line).append('\n')
                line = reader.readLine()
            }
        }
    }
    return stringBuilder.toString()
}