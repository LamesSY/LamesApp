package com.lames.standard.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.lames.standard.R

class LoadingDialogFragment() : DialogFragment() {

    private var message: String? = null
    private var messageView: TextView? = null

    companion object {
        fun newInstance(content: String): LoadingDialogFragment {
            val frag = LoadingDialogFragment()
            val args = Bundle()
            args.putString("content", content)
            frag.arguments = args
            return frag
        }
    }

    override fun onStart() {
        super.onStart()
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = View.inflate(requireContext(), R.layout.dialog_loading_progress, null)
        messageView = view.findViewById(R.id.loadingStr)
        message = arguments?.getString("content")
        updateMessage(message)

        return Dialog(requireContext(), R.style.LoadingDialogTheme).apply {
            setContentView(view)
            setCanceledOnTouchOutside(false)
            setCancelable(false)
            window?.attributes?.gravity = Gravity.CENTER
        }
    }

    fun updateMessage(content: String?) {
        message = content
        messageView?.let { tv ->
            tv.isVisible = !content.isNullOrEmpty()
            tv.text = content
        }
    }
}