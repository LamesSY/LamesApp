package com.lames.standard.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.isGone
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.lames.standard.R

class ActivityLoadingOverlayController(
    private val activity: ComponentActivity,
) : DefaultLifecycleObserver {

    private var overlayView: View? = null

    init {
        activity.lifecycle.addObserver(this)
    }

    fun show(message: String? = null) {
        if (overlayView != null) {
            updateMessage(message)
            return
        }

        val root = activity.findViewById<ViewGroup>(android.R.id.content)
        val view = LayoutInflater.from(activity)
            .inflate(R.layout.dialog_loading_progress, root, false)

        view.isClickable = true
        view.isFocusable = true

        updateMessageInternal(view, message)

        root.addView(view)
        overlayView = view
    }

    fun updateMessage(message: String?) {
        overlayView?.let { updateMessageInternal(it, message) }
    }

    fun dismiss() {
        overlayView?.let {
            (it.parent as? ViewGroup)?.removeView(it)
        }
        overlayView = null
    }

    override fun onDestroy(owner: LifecycleOwner) {
        dismiss()
    }

    private fun updateMessageInternal(view: View, message: String?) {
        val tv = view.findViewById<TextView>(R.id.loadingStr)
        tv?.isGone = message.isNullOrEmpty()
        tv?.text = message ?: ""
    }
}
