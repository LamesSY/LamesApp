package com.lames.standard.webx

import android.content.Context
import androidx.lifecycle.LifecycleCoroutineScope
import com.lames.standard.common.CommonActivity

interface WebHost {
    fun getHostContext(): Context
    fun getHostLifecycleScop(): LifecycleCoroutineScope
    fun setBarTitle(title: String)
    fun finishPage()
    fun getHostActivity(): CommonActivity<*>
}