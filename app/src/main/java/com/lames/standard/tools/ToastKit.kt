package com.lames.standard.tools

import android.content.Context
import android.os.Looper
import android.util.SparseArray
import android.view.View
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes

class ToastKit private constructor(context: Context) {

    companion object {
        private var instance: ToastKit? = null

        fun init(context: Context) {
            if (instance == null) instance = ToastKit(context.applicationContext)
        }

        fun show(content: String, duration: Int = Toast.LENGTH_SHORT) {
            instance?.show(content, duration)
        }

        fun show(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
            instance?.show(stringRes, duration)
        }

        fun makeCustomized(
            @LayoutRes layoutRes: Int,
            action: (View) -> Unit,
            duration: Int = Toast.LENGTH_SHORT,
            gravity: Int? = null, xOffset: Int = 0, yOffset: Int = 0
        ) {
            instance?.makeCustomized(layoutRes, duration, gravity, xOffset, yOffset, action)
        }
    }

    private val normalToast by lazy {
        Toast.makeText(
            context.applicationContext,
            "",
            Toast.LENGTH_SHORT
        )
    }
    private val toastPool: SparseArray<Toast> = SparseArray()
    private val appContext = context.applicationContext

    fun show(content: String, duration: Int = Toast.LENGTH_SHORT) {
        if (Looper.getMainLooper().thread !== Thread.currentThread()) return
        normalToast.setText(content)
        normalToast.duration = duration
        normalToast.show()
    }

    fun show(@StringRes stringRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        if (stringRes == 0 || Looper.getMainLooper().thread !== Thread.currentThread()) return
        normalToast.setText(stringRes)
        normalToast.duration = duration
        normalToast.show()
    }

    fun makeCustomized(
        @LayoutRes layoutRes: Int,
        duration: Int = Toast.LENGTH_SHORT,
        gravity: Int? = null, xOffset: Int = 0, yOffset: Int = 0,
        action: (View) -> Unit
    ) {
        var toast = toastPool[layoutRes]
        if (toast == null) {
            toast = Toast(appContext)
            toast.view = View.inflate(appContext, layoutRes, null)
            toastPool[layoutRes] = toast
        }
        gravity?.let { toast.setGravity(gravity, xOffset, yOffset) }
        action(toast.view!!)
        toast.duration = duration
        toast.show()
    }
}