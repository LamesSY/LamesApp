package com.lames.standard.dialog

import android.view.Gravity
import android.view.LayoutInflater
import androidx.fragment.app.FragmentManager
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.view.TimePickerView
import com.lames.standard.R
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants
import com.lames.standard.common.GlobalVar
import com.lames.standard.databinding.DialogFragmentDateSetBinding
import com.lames.standard.tools.forAttrColor
import com.lames.standard.tools.forColor
import com.lames.standard.tools.onClick
import java.util.Calendar
import java.util.Date

/**
 * 竖直滑动滚轮形式的日期选择器
 */
class DateSetDialogFragment : CommonDialogFragment<DialogFragmentDateSetBinding>() {

    private var pickerView: TimePickerView? = null

    var title: String? = null
    var defaultDate = Calendar.getInstance()
    var onSetPickerView: (TimePickerBuilder.() -> Unit)? = null
    var onSelectedDate: ((Date) -> Unit)? = null
    var minDate: Calendar? = null
    var maxDate: Calendar? = null

    override fun onStart() {
        super.onStart()
        dialog?.window?.let {
            it.setLayout(GlobalVar.obtain().screenWith, it.attributes.height)
        }
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun getViewBinding(inflater: LayoutInflater): DialogFragmentDateSetBinding {
        return DialogFragmentDateSetBinding.inflate(inflater)
    }

    override fun initialization() {
        title?.let { binding.title.text = it }
        setPickerView()
    }

    override fun bindEvent() {
        binding.cancel.onClick { dismiss() }

        binding.confirm.onClick {
            pickerView?.returnData()
            dismiss()
        }
    }

    private fun setPickerView() {
        val builder = TimePickerBuilder(context) { date, _ -> onSelectedDate?.invoke(date) }
            .setLayoutRes(R.layout.view_picker_view) {}
            .setType(booleanArrayOf(true, true, true, false, false, false))
            .setLabel(
                Constants.Project.EMPTY_STR,
                Constants.Project.EMPTY_STR,
                Constants.Project.EMPTY_STR,
                Constants.Project.EMPTY_STR,
                Constants.Project.EMPTY_STR,
                Constants.Project.EMPTY_STR
            )
            .setContentTextSize(24)//滚轮文字大小
            .setTitleSize(15)
            .setSubCalSize(15)
            .isCenterLabel(false)
            .setDecorView(binding.pickerContainer)
            .isDialog(false)
            .setDividerColor(forAttrColor(requireActivity(), R.attr.divider_1))
            .setOutSideColor(forAttrColor(requireActivity(), R.attr.windowBg_1))
            .setTextColorOut(forColor(R.color.divider_1))
            .setTextColorCenter(forAttrColor(requireActivity(), R.attr.themeColor))
            .setOutSideCancelable(false)
            .setGravity(Gravity.CENTER)
        builder.setDate(defaultDate)
        onSetPickerView?.invoke(builder)

        if (minDate != null) {
            if (maxDate != null) builder.setRangDate(minDate, maxDate)
            else builder.setRangDate(minDate, Calendar.getInstance())
        }
        pickerView = builder.build()
        pickerView?.show(false)

        pickerView?.dialogContainerLayout?.onClick {

        }
        pickerView?.setKeyBackCancelable(false)
    }

    companion object {
        fun show(
            fragmentManager: FragmentManager,
            onSetPickerView: (TimePickerBuilder.() -> Unit)? = null,
            onSelectedDate: ((Date) -> Unit)? = null,
        ) {
            val f = DateSetDialogFragment()
            f.onSetPickerView = onSetPickerView
            f.onSelectedDate = onSelectedDate
            f.show(fragmentManager, null)
        }
    }

}