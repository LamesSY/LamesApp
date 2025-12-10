package com.lames.standard.dialog

import android.view.LayoutInflater
import android.widget.CalendarView
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.databinding.DialogFragmentCalendarSelectBinding
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.onClick
import java.util.Calendar

class CalendarSelectDialogFragment : CommonDialogFragment<DialogFragmentCalendarSelectBinding>() {

    var initDate: Long = 0L
    var onSelect: ((date: Long) -> Unit)? = null
    var initCalendarView: ((calendarView: CalendarView) -> Unit)? = null

    override fun getViewBinding(inflater: LayoutInflater): DialogFragmentCalendarSelectBinding {
        return DialogFragmentCalendarSelectBinding.inflate(inflater)
    }

    override fun initialization() {
        val now = Calendar.getInstance()
        if (initDate <= 0) initDate = now.timeInMillis
        binding.calendarView.date = initDate
        binding.calendarView.maxDate = now.timeInMillis

        val minCal = Calendar.getInstance().also { it.add(Calendar.YEAR, -2) }
        binding.calendarView.minDate = minCal.timeInMillis
        initCalendarView?.invoke(binding.calendarView)
    }

    override fun bindEvent() {
        binding.cancel.onClick {
            dismiss()
        }

        binding.calendarView.setOnDateChangeListener { view, y, m, d ->
            LogKit.d(javaClass.simpleName, "$y-$m-$d")
            val calendar = Calendar.getInstance().also { it.set(y, m, d) }
            binding.calendarView.date = calendar.timeInMillis
        }

        binding.confirm.onClick {
            val now = binding.calendarView.date
            onSelect?.invoke(now)
            dismiss()
        }
    }
}