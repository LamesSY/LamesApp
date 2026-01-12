package com.lames.standard.common

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lames.standard.R
import java.lang.ref.WeakReference

class DialogLoadingController(activity: FragmentActivity) : DefaultLifecycleObserver {

    private val activityRef = WeakReference(activity)
    private val handler = Handler(Looper.getMainLooper())

    private var wantShow = false
    private var message: String = ""

    private var dialog: Dialog? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    fun show(msg: String = "") {
        wantShow = true
        message = msg

        handler.removeCallbacksAndMessages(null)
        handler.post { commit() }
    }

    fun dismiss() {
        wantShow = false

        handler.removeCallbacksAndMessages(null)
        handler.post { commit() }
    }

    private fun commit() {
        val activity = activityRef.get() ?: return
        if (activity.isFinishing || activity.isDestroyed) return

        if (wantShow) {
            if (dialog == null) {
                dialog = createDialog(activity)
            }
            if (dialog?.isShowing != true) {
                dialog?.show()
            }
            val tv = dialog?.findViewById<TextView>(R.id.loadingStr)
            tv?.isVisible = message.isNotEmpty()
            tv?.text = message
        } else {
            dialog?.dismiss()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        handler.removeCallbacksAndMessages(null)
        dialog?.dismiss()
        dialog = null
        activityRef.clear()
    }

    private fun createDialog(activity: Activity): Dialog {
        val view = View.inflate(activity, R.layout.dialog_loading_progress, null)
        return Dialog(activity, R.style.LoadingDialogTheme).apply {
            setContentView(view)
            setCancelable(false)
            window?.setGravity(Gravity.CENTER)
        }
    }
}