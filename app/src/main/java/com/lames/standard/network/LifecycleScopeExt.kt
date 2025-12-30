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
            if (e is SessionExpiredException) {
                showErrorToast(e.errorMsg)
                e.printStackTrace()
                CommonApp.obtain<App>().onSignOut()
                return@launch
            }
            if (onError == null) {
                //不提示CancellationException错误，但仍需打印在log中
                if (isActive) showErrorToast(e.errorMsg)
                LogKit.e(javaClass.simpleName, e.errorMsg, true)
                e.printStackTrace()
            } else try {
                onError.invoke(e)
            } catch (e: Throwable) {
                e.printStackTrace()
            }
        } finally {
            onFinally?.invoke()
        }
    }
}

fun LifecycleCoroutineScope.launchX(block: suspend CoroutineScope.() -> Unit): Job {
    return execute(block)
}
