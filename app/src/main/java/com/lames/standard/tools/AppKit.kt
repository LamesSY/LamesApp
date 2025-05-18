package com.lames.standard.tools

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import java.util.*
import kotlin.system.exitProcess


class AppKit private constructor() : Application.ActivityLifecycleCallbacks {

    private var activityStack: Stack<Activity> = Stack()

    fun init(app: Application) = app.registerActivityLifecycleCallbacks(this)

    companion object {
        @JvmStatic
        fun obtain(): AppKit = Holder.instance

        private object Holder {
            internal var instance = AppKit()
        }
    }

    private fun log(msg: Any, tag: String = this.javaClass.name) = Log.v(tag, msg.toString())

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStack.add(activity)
        log("stack size:${activityStack.size}, ${activity.javaClass.simpleName} created")
    }

    override fun onActivityStarted(activity: Activity) {
        log("${activity.javaClass.simpleName} started")
    }

    override fun onActivityResumed(activity: Activity) {
        log("${activity.javaClass.simpleName} resumed")
    }

    override fun onActivityPaused(activity: Activity) {
        log("${activity.javaClass.simpleName} paused")
    }

    override fun onActivityStopped(activity: Activity) {
        log("${activity.javaClass.simpleName} stopped")
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        log("${activity.javaClass.simpleName} saved state")
    }

    override fun onActivityDestroyed(activity: Activity) {
        activityStack.remove(activity)
        log("stack size:${activityStack.size}, ${activity.javaClass.simpleName} removed")
    }

    fun getActivityStackSize(): Int = activityStack.size

    fun topActivity(): Activity? = activityStack.takeIf { it.size > 0 }?.lastElement()

    fun bottomActivity(): Activity? = activityStack.takeIf { it.size > 0 }?.lastElement()

    fun finishAllActivity(except: Activity? = null) {
        activityStack.forEach { if (except != it) it.finish() }
    }

    fun restartApp(context: Context) = context.applicationContext.restartApp()

    @Synchronized
    fun terminateApp() {
        finishAllActivity()
        exitProcess(0)
    }

    @Synchronized
    fun quit() = finishAllActivity()
}