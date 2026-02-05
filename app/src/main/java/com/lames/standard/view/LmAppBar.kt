package com.lames.standard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.lames.standard.R
import com.lames.standard.databinding.ViewAppBarBinding
import com.lames.standard.image.ImageKit
import com.lames.standard.tools.forColor
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick

class LmAppBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        const val HEIGHT_DP = 50
    }

    private val binding by lazy { ViewAppBarBinding.inflate(LayoutInflater.from(context), this, true) }
    init {
        context.withStyledAttributes(attrs, R.styleable.LmAppBar) {
            val style = this.getInteger(R.styleable.LmAppBar_barStyle, 0)
            when (style) {
                1 -> setTranTheme()
                else -> setNormalTheme()
            }
            val showClose = this.getBoolean(R.styleable.LmAppBar_showClose, false)
            binding.appClose.isVisible = showClose
        }
    }

    fun setTitle(resId: Int) {
        setTitle(forString(resId))
    }

    fun setTitle(content: String) {
        binding.appTitle.text = content
    }

    fun setNormalTheme() {
        binding.lmAppBar.setBackgroundColor(forColor(R.color.windowBg_1))
    }

    fun setTranTheme() {
        binding.lmAppBar.setBackgroundColor(0)
    }

    fun onAppBackIconClick(onClick: (() -> Unit)) {
        binding.appBack.onClick { onClick.invoke() }
    }

    fun onAppCloseIconClick(onClick: (() -> Unit)) {
        binding.appClose.onClick { onClick.invoke() }
    }

    fun onRightTextClick(onClick: (() -> Unit)) {
        binding.appRightTv.onClick { onClick.invoke() }
    }

    fun onRightIconClick(onClick: (() -> Unit)) {
        binding.appRightIcon.onClick { onClick.invoke() }
    }

    fun setRightIcon(resId: Int) {
        binding.appRightIcon.isVisible = resId > 0
        if (resId > 0) binding.appRightIcon.setImageResource(resId)
    }

    fun setRightIcon(url: String) {
        ImageKit.with(context).load(url).into(binding.appRightIcon)
    }
}