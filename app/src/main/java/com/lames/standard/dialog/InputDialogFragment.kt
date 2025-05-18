package com.lames.standard.dialog

import android.view.LayoutInflater
import android.widget.EditText
import com.lames.standard.R
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.databinding.DialogFragmentInputBinding
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast

class InputDialogFragment : CommonDialogFragment<DialogFragmentInputBinding>() {

    var title: String = EMPTY_STR
    var initContent: String = EMPTY_STR
    var editSet: EditText.() -> Unit = {}
    var onCancel: () -> Unit = {}
    var onConfirm: (content: String) -> Unit = {}

    override fun getViewBinding(inflater: LayoutInflater) =
        DialogFragmentInputBinding.inflate(inflater)

    override fun initialization() {
        if (initContent.isNotEmpty()) binding.editInput.setText(initContent)
        binding.title.text = title
        binding.editInput.editSet()
    }

    override fun bindEvent() {
        binding.cancel.onClick {
            onCancel.invoke()
            dismiss()
        }

        binding.confirm.onClick {
            if (binding.editInput.text.isNullOrEmpty()) {
                showErrorToast(R.string.hint_plz_input)
                return@onClick
            }
            onConfirm.invoke(binding.editInput.text.toString())
            dismiss()
        }
    }

}