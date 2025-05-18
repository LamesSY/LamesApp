package com.lames.standard.tools

import android.util.Log
import com.lames.standard.event.EventDict
import com.lames.standard.event.LogEventKit
import com.lames.standard.BuildConfig
import com.lames.standard.common.GlobalVar
import java.io.File
import java.io.FileWriter

object LogKit {

    init {
        Log.d("DEF_TAG", "LogKit is init")
    }

    private const val DEF_TAG = "LogKit"

    private const val MAX_NUM = 4000

    private var isDebug = BuildConfig.DEBUG

    var saveToFile: Boolean = false

    fun v(tag: String = DEF_TAG, msg: String, up: Boolean = false) =
        printLog(Log.VERBOSE, tag, msg, up)

    fun d(tag: String = DEF_TAG, msg: String, up: Boolean = false) =
        printLog(Log.DEBUG, tag, msg, up)

    fun i(tag: String = DEF_TAG, msg: String, up: Boolean = false) =
        printLog(Log.INFO, tag, msg, up)

    fun w(tag: String = DEF_TAG, msg: String, up: Boolean = false) =
        printLog(Log.WARN, tag, msg, up)

    fun e(tag: String = DEF_TAG, msg: String, up: Boolean = true) =
        printLog(Log.ERROR, tag, msg, up)

    private fun detail(): String? {
        val stackTrace = Thread.currentThread().stackTrace
        stackTrace.forEach {
            if (it.isNativeMethod) return@forEach
            if (it.className == Thread::class.java.name) return@forEach
            if (it.className == LogKit::class.java.name) return@forEach
            return "[(${it.fileName}:${it.lineNumber}) ${it.methodName}]"
        }
        return null
    }

    private fun printLog(type: Int, tag: String = DEF_TAG, message: String, up: Boolean) {
        val msg = detail()?.let { d -> "$d -> $message" } ?: message
        upEvent(logLevelStr(type), tag, message, up)
        if (isDebug.not()) return
        if (saveToFile) saveToFile(tag, msg)
        when (type) {
            Log.VERBOSE -> msg.chunked(MAX_NUM).forEach { Log.v(tag, it) }
            Log.DEBUG -> msg.chunked(MAX_NUM).forEach { Log.d(tag, it) }
            Log.INFO -> msg.chunked(MAX_NUM).forEach { Log.i(tag, it) }
            Log.WARN -> msg.chunked(MAX_NUM).forEach { Log.w(tag, it) }
            Log.ERROR -> msg.chunked(MAX_NUM).forEach { Log.e(tag, it) }
        }
    }

    private fun saveToFile(tag: String, msg: String) = runCatching {
        val now = System.currentTimeMillis()
        val content = StringBuilder()
            .append(now.toPatternString("yyyyMMdd-HH:mm:ss:SSS"))
            .append("|")
            .append(tag)
            .append("|")
            .append(msg)
            .append("\n")
        val file =
            File(GlobalVar.obtain().appRootPath + File.separator + "log_${now.toPatternString("yyyyMMdd")}.txt")
        synchronized(LogKit::class.java) {
            FileWriter(file, true).use { it.write(content.toString()) }
        }

    }

    private fun upEvent(level: String, tag: String, msg: String, up: Boolean) {
        if (up) {
            LogEventKit.obtain().logEvent(
                EventDict.app_log,
                mapOf(EventDict.opvs1 to level, EventDict.opvs2 to tag, EventDict.opvs3 to msg)
            )
        }
    }

    private fun logLevelStr(levelInt: Int) = when (levelInt) {
        2 -> "v"
        3 -> "d"
        4 -> "i"
        5 -> "w"
        6 -> "e"
        else -> "a"
    }

}