package com.lames.standard.common

import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import com.lames.standard.R

class DialogLoadingController(private val activity: Activity) {

    private val handler = Handler(Looper.getMainLooper())

    private var wantShow = false
    private var message: String = ""

    private val dialog: Dialog by lazy {
        val view = View.inflate(activity, R.layout.dialog_loading_progress, null)
        Dialog(activity, R.style.LoadingDialogTheme).apply {
            setContentView(view)
            setCanceledOnTouchOutside(false)
            window?.setGravity(Gravity.CENTER)
            setCancelable(false)
            setOnKeyListener { _, keyCode, event ->
                keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP
            }
        }
    }

    fun show(msg: String = "") {
        wantShow = true
        message = msg

        handler.removeCallbacksAndMessages(null)
        handler.post {
            commit()
        }
    }

    fun dismiss() {
        wantShow = false

        handler.removeCallbacksAndMessages(null)
        handler.post {
            commit()
        }
    }

    private fun commit() {
        if (activity.isFinishing || activity.isDestroyed) return

        if (wantShow) {
            if (!dialog.isShowing) {
                dialog.show()
            }
            // 更新文案（如果有 TextView）
            val tv = dialog.findViewById<TextView>(R.id.loadingStr)
            tv.isVisible = message.isNotEmpty()
            tv.text = message
        } else {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
    }

    fun forceDismiss() {
        wantShow = false
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}