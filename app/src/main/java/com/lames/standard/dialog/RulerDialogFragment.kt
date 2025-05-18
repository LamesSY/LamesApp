package com.lames.standard.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.lames.standard.R
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.common.GlobalVar
import com.lames.standard.databinding.DialogFragmentRulerBinding
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick
import com.lames.standard.view.RulerView

class RulerDialogFragment : CommonDialogFragment<DialogFragmentRulerBinding>() {

    var title: String = EMPTY_STR
    var unit: String = EMPTY_STR
    var tip: String = EMPTY_STR
    var isDecimal: Boolean = false
    var initRulerView: ((ruler: RulerView) -> Unit)? = null
    var onConfirm: ((value: Float) -> Unit)? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(GlobalVar.obtain().screenWith, it.attributes.height)
        }
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun getViewBinding(inflater: LayoutInflater): DialogFragmentRulerBinding {
        return DialogFragmentRulerBinding.inflate(inflater)
    }

    override fun initialization() {
        binding.title.text = title
        binding.rulerUnit.text = unit
        binding.tip.text = tip
    }

    override fun bindEvent() {
        binding.rulerView.setOnValueChangedListener {
            binding.rulerValue.text = "${if (isDecimal) it.toInt() else it}"
        }

        binding.cancel.onClick { dismiss() }

        binding.confirm.onClick {
            onConfirm?.invoke(binding.rulerView.currentValue)
            dismiss()
        }

        initRulerView?.invoke(binding.rulerView)
    }

    companion object {
        fun showUserM62(
            fragmentManager: FragmentManager,
            initValue: Float = 80f,
            onConfirm: ((value: Float) -> Unit)? = null
        ) {
            RulerDialogFragment().apply {
                title = forString(R.string.m62)
                unit = "cm"
                tip = EMPTY_STR
                isDecimal = false
                initRulerView = { it.setValue(30f, 300f, initValue, 0.1f, 10) }
                this.onConfirm = onConfirm
            }.show(fragmentManager, null)
        }

        fun showUserHeight(
            fragmentManager: FragmentManager,
            initValue: Float = 150f,
            onConfirm: ((value: Float) -> Unit)? = null
        ) {
            RulerDialogFragment().apply {
                title = forString(R.string.height)
                unit = "cm"
                tip = EMPTY_STR
                isDecimal = false
                initRulerView = { it.setValue(90f, 220f, initValue, 0.1f, 10) }
                this.onConfirm = onConfirm
            }.show(fragmentManager, null)
        }

        fun showUserWeight(
            fragmentManager: FragmentManager,
            initValue: Float = 60f,
            onConfirm: ((value: Float) -> Unit)? = null
        ) {
            RulerDialogFragment().apply {
                title = forString(R.string.weight)
                unit = "kg"
                tip = EMPTY_STR
                isDecimal = false
                initRulerView = { it.setValue(30f, 120f, initValue, 0.1f, 10) }
                this.onConfirm = onConfirm
            }.show(fragmentManager, null)
        }
    }

}