package com.lames.standard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.lames.standard.R
import com.lames.standard.databinding.ViewAppBarBinding
import com.lames.standard.tools.forColor
import com.lames.standard.tools.forString
import com.lames.standard.tools.onClick

class LmAppBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding by lazy { ViewAppBarBinding.inflate(LayoutInflater.from(context), this, true) }

    var onAppBackClick: (() -> Unit)? = null
    var onAppCloseClick: (() -> Unit)? = null
    var onAppRightIconClick: (() -> Unit)? = null
    var onAppRightTvClick: (() -> Unit)? = null

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
        binding.appBack.onClick { onAppBackClick?.invoke() }
        binding.appClose.onClick { onAppCloseClick?.invoke() }
        binding.appRightIcon.onClick { onAppRightIconClick?.invoke() }
        binding.appRightTv.onClick { onAppRightTvClick?.invoke() }
    }

    fun setTitle(resId: Int) {
        setTitle(forString(resId))
    }

    fun setTitle(content: String) {
        binding.appTitle.text = content
    }

    private fun setNormalTheme() {
        binding.lmAppBar.setBackgroundColor(forColor(R.color.windowBg_1))
    }

    private fun setTranTheme() {
        binding.lmAppBar.setBackgroundColor(0)
    }
}