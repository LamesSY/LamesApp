package com.lames.standard.event

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.lames.standard.BuildConfig
import com.lames.standard.entity.LogEvent
import com.lames.standard.module.launch.SplashActivity
import com.lames.standard.network.Api
import com.lames.standard.network.postRequest
import com.lames.standard.tools.LogKit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import rxhttp.tryAwait
import rxhttp.wrapper.param.toAwaitResultData
import java.util.WeakHashMap

class LogEventKit private constructor() : Application.ActivityLifecycleCallbacks {

    private val TAG = this::class.java.simpleName

    companion object {

        fun obtain(): LogEventKit = Holder.instance

        private object Holder {
            var instance = LogEventKit()
        }
    }

    private val logEventWorkerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val resumeTimeMap = WeakHashMap<Context, Long>()

    fun init(app: Application) {
        app.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityResumed(activity: Activity) {
        resumeTimeMap[activity] = System.currentTimeMillis()
    }

    override fun onActivityPaused(activity: Activity) {
        val pageName = activity.javaClass.simpleName
        val resumeTime = resumeTimeMap[activity] ?: 0
        val pauseTime = System.currentTimeMillis()
        resumeTimeMap.remove(activity)
        if (pauseTime - resumeTime > 10) {
            LogKit.d(TAG, "page name: $pageName, stay for: ${pauseTime - resumeTime} milliseconds")
            logEvent(
                EventDict.screen_view,
                mapOf(
                    "opvs1" to pageName,
                    "opvl1" to resumeTime,
                    "opvl2" to pauseTime,
                    "opvl3" to pauseTime - resumeTime
                )
            )
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        LogKit.d(TAG, "onActivityCreated: ${activity::class.java.name}")
    }

    override fun onActivityDestroyed(activity: Activity) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    /**
     * 记录日志
     */
    fun logEvent(opType: String, mapParam: Map<String, Any?>? = null) {
        if (opType.isEmpty()) {
            return
        }
        logEventWorkerScope.launch {
            val event = LogEvent.newEvent().apply {
                this.op_type = opType
            }
            mapParam?.forEach {
                if (it.key == EventDict.opvs1) {
                    event.opvs1 = it.value.toString()
                }
                if (it.key == EventDict.opvs2) {
                    event.opvs2 = it.value.toString()
                }
                if (it.key == EventDict.opvs3) {
                    event.opvs3 = it.value.toString()
                }
                if (it.key == EventDict.opvs4) {
                    event.opvs4 = it.value.toString()
                }
                if (it.key == EventDict.opvs5) {
                    event.opvs5 = it.value.toString()
                }
                if (it.key == EventDict.opvs6) {
                    event.opvs6 = it.value.toString()
                }
                if (it.key == EventDict.opvl1) {
                    event.opvl1 = it.value.toString().toLong()
                }
                if (it.key == EventDict.opvl2) {
                    event.opvl2 = it.value.toString().toLong()
                }
                if (it.key == EventDict.opvl3) {
                    event.opvl3 = it.value.toString().toLong()
                }
                if (it.key == EventDict.opvl4) {
                    event.opvl4 = it.value.toString().toLong()
                }
                if (it.key == EventDict.opvl5) {
                    event.opvl5 = it.value.toString().toLong()
                }
                if (it.key == EventDict.opvd1) {
                    event.opvd1 = it.value.toString().toDouble()
                }
                if (it.key == EventDict.opvd2) {
                    event.opvd2 = it.value.toString().toDouble()
                }
                if (it.key == EventDict.opvd3) {
                    event.opvd3 = it.value.toString().toDouble()
                }
                if (it.key == EventDict.opvd4) {
                    event.opvd4 = it.value.toString().toDouble()
                }
                if (it.key == EventDict.opvd5) {
                    event.opvd5 = it.value.toString().toDouble()
                }
            }
            //LogKit.d(TAG, "上报事件 opType:$opType params:${mapParam.toString()}")
            if (BuildConfig.DEBUG) {
                return@launch
            }
            uploadLogEvent(mutableListOf(event))
        }
    }

    /**
     * api上报
     */
    private suspend fun uploadLogEvent(list: MutableList<LogEvent>) {
        if (list.isNotEmpty()) {
            val res = postRequest(
                Api.Event.UPLOAD_EVENTS,
                hashMapOf("eventParamList" to list)
            ).toAwaitResultData<Int>().tryAwait()
        }
    }

}