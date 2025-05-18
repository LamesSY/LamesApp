package com.lames.standard.event

import com.lames.standard.tools.LogKit
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import kotlin.system.exitProcess

class CrashHandler : Thread.UncaughtExceptionHandler {

    private val TAG = this::class.java.simpleName

    companion object {

        fun obtain(): CrashHandler = Holder.instance

        private object Holder {
            var instance = CrashHandler()
        }

    }

    private var mDefaultCrashHandler: Thread.UncaughtExceptionHandler? = null//系统默认的异常处理

    fun init() {
        mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        e.printStackTrace()
        try {
            LogEventKit.obtain().logEvent(
                EventDict.crash_handler,
                mapOf(EventDict.opvs1 to formatStackTrace(e), EventDict.opvs2 to t.name)
            )
        } catch (e: Exception) {
            LogKit.e(TAG, "上报崩溃错误 ${e.message}")
        }
        if (mDefaultCrashHandler == null) {
            exitProcess(0)
        } else {
            mDefaultCrashHandler?.uncaughtException(t, e)
        }
    }

    /**
     * 错误信息转为string
     */
    private fun formatStackTrace(throwable: Throwable?): String {
        if (throwable == null) return ""
        var rtn = throwable.stackTrace.toString()
        try {
            val writer: Writer = StringWriter()
            val printWriter = PrintWriter(writer)
            throwable.printStackTrace(printWriter)
            printWriter.flush()
            writer.flush()
            rtn = writer.toString()
            printWriter.close()
            writer.close()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (ex: java.lang.Exception) {
        }
        return rtn
    }

}