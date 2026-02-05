package com.lames.standard.common

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import com.lames.standard.R
import com.lames.standard.tools.forString

abstract class CommonDialogFragment<T : ViewBinding> : DialogFragment() {

    private var _binding: T? = null

    protected val binding: T get() = _binding!!

    protected var loadingDialog = DialogLoadingOverlayController(this)

    protected val parentActivity: CommonActivity<*> by lazy { requireActivity() as CommonActivity<*> }

    protected open fun dialogType(): Int = 0

    protected abstract fun getViewBinding(inflater: LayoutInflater): T

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = getViewBinding(layoutInflater)
        val dialog = when (dialogType()) {
            0 -> AlertDialog.Builder(requireActivity(), R.style.DialogFragmentTheme)
                .setView(_binding!!.root).create()

            else -> Dialog(requireActivity(), R.style.DialogFragmentTheme).also {
                it.setContentView(
                    _binding!!.root
                )
            }
        }
        dialog.setOnShowListener {

        }
        return dialog
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(resources.displayMetrics.widthPixels * 8 / 10, it.attributes.height)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val v = super.onCreateView(inflater, container, savedInstanceState)
        initialization()
        bindEvent()
        doExtra()
        return v
    }

    protected open fun initialization() {}
    protected open fun bindEvent() {}
    protected open fun doExtra() {}

    protected fun showProgressDialog(content: String = Constants.Project.EMPTY_STR) {
        loadingDialog.show(content)
    }

    protected fun showProgressDialog(@StringRes content: Int) {
        showProgressDialog(forString(content))
    }

    protected fun dismissProgressDialog() {
        loadingDialog.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}