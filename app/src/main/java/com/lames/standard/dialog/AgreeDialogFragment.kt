package com.lames.standard.dialog

import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants
import com.lames.standard.databinding.DialogFragmentAgreeBinding
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.tools.AppKit

class AgreeDialogFragment : CommonDialogFragment<DialogFragmentAgreeBinding>() {

    var title: String? = null
    var content: String? = null
    var showCancelBtn: Boolean = true

    var onCancel: (() -> Unit)? = null
    var onConfirm: (() -> Unit)? = null

    override fun getViewBinding(inflater: LayoutInflater) =
        DialogFragmentAgreeBinding.inflate(inflater)


    override fun initialization() {
        binding.alertContent.text = content ?: Constants.Project.EMPTY_STR
        binding.cancel.isVisible = showCancelBtn

        setCancelable(false)
    }

    override fun bindEvent() {
        binding.cancel.setOnClickListener {
            AppConfigMMKV.agreeDialog = false
            AppKit.obtain().quit()
            onCancel?.invoke()
            dismiss()
        }

        binding.confirm.setOnClickListener {
            AppConfigMMKV.agreeDialog = true
            onConfirm?.invoke()
            dismiss()
        }
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager, title: String, content: String,
            initView: (AgreeDialogFragment.() -> Unit)? = null,
            onConfirm: (() -> Unit)? = null,
            onCancel: (() -> Unit)? = null,
        ) {
            val f = AgreeDialogFragment()
            f.title = title
            f.content = content
            f.onConfirm = onConfirm
            f.onConfirm = onConfirm
            f.onCancel = onCancel
            initView?.invoke(f)
            f.show(fragmentManager, null)
        }
    }


}