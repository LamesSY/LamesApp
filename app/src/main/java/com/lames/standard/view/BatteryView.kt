package com.lames.standard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.lames.standard.databinding.ViewBatteryIconBinding

class BatteryView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by lazy {
        ViewBatteryIconBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    init {
        binding.batteryProgress.progress = 100
    }

    fun setBattery(battery: Int, max: Int = 100) {
        val p = if (battery < 0) 0 else if (battery > 100) 100 else battery
        binding.batteryProgress.progress = p
    }
}