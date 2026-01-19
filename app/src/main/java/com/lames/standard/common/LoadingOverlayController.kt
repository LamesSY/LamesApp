package com.lames.standard.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isGone
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lames.standard.R

class LoadingOverlayController(
    private val activity: ComponentActivity,
) : DefaultLifecycleObserver {

    private var overlayView: View? = null
    private var backCallback: OnBackPressedCallback? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    fun show(message: String? = null) {
        if (overlayView != null) {
            updateMessage(message)
            return
        }

        val root = activity.window.decorView as ViewGroup

        val view = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_loading_progress, root, false)

        view.isClickable = true
        view.isFocusable = true

        updateMessageInternal(view, message)

        root.addView(view)
        overlayView = view

        interceptBack()
    }

    fun updateMessage(message: String?) {
        overlayView?.let {
            updateMessageInternal(it, message)
        }
    }

    fun dismiss() {
        overlayView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
        }
        overlayView = null
        removeBackInterceptor()
    }

    // ---------- Back ----------
    private fun interceptBack() {
        if (backCallback != null) return

        backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 什么都不做，屏蔽返回
            }
        }.also {
            activity.onBackPressedDispatcher.addCallback(activity, it)
        }
    }

    private fun removeBackInterceptor() {
        backCallback?.remove()
        backCallback = null
    }

    // ---------- Lifecycle ----------
    override fun onDestroy(owner: LifecycleOwner) {
        dismiss()
        activity.lifecycle.removeObserver(this)
    }

    private fun updateMessageInternal(view: View, message: String?) {
        val v = view.findViewById<TextView>(R.id.loadingStr)
        v?.isGone = message.isNullOrEmpty()
        v?.text = message ?: ""
    }
}
