package com.lames.standard.dialog

import android.graphics.Typeface
import android.view.LayoutInflater
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants
import com.lames.standard.databinding.DialogFragmentAlertBinding
import com.lames.standard.tools.gone

class AlertDialogFragment : CommonDialogFragment<DialogFragmentAlertBinding>() {

    var title: String? = null
    var content: String? = null
    var cancelStr: String? = null
    var confirmStr: String? = null
    var showCancelBtn: Boolean = true

    var onCancel: (() -> Unit)? = null
    var onConfirm: (() -> Unit)? = null

    override fun getViewBinding(inflater: LayoutInflater) =
        DialogFragmentAlertBinding.inflate(inflater)

    override fun initialization() {
        binding.alertTitle.text = title ?: Constants.Project.EMPTY_STR
        binding.alertContent.text = content ?: Constants.Project.EMPTY_STR
        cancelStr?.let { binding.cancel.text = it }
        confirmStr?.let { binding.confirm.text = it }
        binding.cancel.isVisible = showCancelBtn

        //没有标题时改变下样式
        if (title.isNullOrEmpty()) {
            binding.alertContent.gone()
            binding.alertTitle.text = content
            binding.alertTitle.typeface = Typeface.DEFAULT
        }
    }

    override fun bindEvent() {
        binding.cancel.setOnClickListener {
            onCancel?.invoke()
            dismiss()
        }

        binding.confirm.setOnClickListener {
            onConfirm?.invoke()
            dismiss()
        }
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager, title: String, content: String,
            initView: (AlertDialogFragment.() -> Unit)? = null,
            onConfirm: (() -> Unit)? = null,
        ) {
            val f = AlertDialogFragment()
            f.title = title
            f.content = content
            f.onConfirm = onConfirm
            initView?.invoke(f)
            f.show(fragmentManager, null)
        }

        fun show(
            fragmentManager: FragmentManager,
            initView: (AlertDialogFragment.() -> Unit)? = null
        ) {
            val f = AlertDialogFragment()
            initView?.invoke(f)
            f.show(fragmentManager, null)
        }
    }


}