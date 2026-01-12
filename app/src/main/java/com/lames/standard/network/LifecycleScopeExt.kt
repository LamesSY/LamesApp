package com.lames.standard.network

import androidx.lifecycle.LifecycleCoroutineScope
import com.lames.standard.App
import com.lames.standard.common.CommonApp
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.showErrorToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

fun LifecycleCoroutineScope.execute(
    block: suspend CoroutineScope.() -> Unit,
    onError: ((Throwable) -> Unit)? = null,
    onStart: (() -> Unit)? = null,
    onFinally: (() -> Unit)? = null,
): Job {
    return launch {
        try {
            onStart?.invoke()
            block()
        } catch (e: Throwable) {
            LogKit.e(javaClass.simpleName, e.errorMsg, true)
            e.printStackTrace()
            if (e is SessionExpiredException) {
                showErrorToast(e.errorMsg)
                CommonApp.obtain<App>().onSignOut()
                return@launch
            }
            if (isActive) onError?.invoke(e) ?: showErrorToast(e.errorMsg)
        } finally {
            if (isActive) onFinally?.invoke()
        }
    }
}

fun LifecycleCoroutineScope.launchX(block: suspend CoroutineScope.() -> Unit): Job {
    return execute(block)
}
