package com.lames.standard.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.lames.standard.R

class DialogLoadingOverlayController(
    private val dialogFragment: DialogFragment,
) {

    private var overlayView: View? = null

    fun show(message: String? = null) {
        val window = dialogFragment.dialog?.window ?: return
        val root = window.decorView as? ViewGroup ?: return

        if (overlayView != null) {
            updateMessage(message)
            return
        }

        val view = LayoutInflater.from(window.context)
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

    private fun updateMessageInternal(view: View, message: String?) {
        val tv = view.findViewById<TextView>(R.id.loadingStr)
        tv?.isGone = message.isNullOrEmpty()
        tv?.text = message ?: ""
    }
}
