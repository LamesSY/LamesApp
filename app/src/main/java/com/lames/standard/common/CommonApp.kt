package com.lames.standard.common

import android.app.Application


open class CommonApp : Application() {

    companion object {
        private var instance: CommonApp? = null

        @Suppress("UNCHECKED_CAST")
        @JvmStatic
        fun <T : CommonApp> obtain(): T = instance as T
    }

    final override fun onCreate() {
        super.onCreate()
        instance = this
        doCommonInit()
    }

    open fun doCommonInit() {}

    open fun onSignOut() {}
}