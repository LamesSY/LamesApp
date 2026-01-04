package com.lames.standard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.lames.standard.R
import com.lames.standard.databinding.ViewDefaultLoadingBinding
import com.lames.standard.tools.gone
import com.lames.standard.tools.visible

class PageStateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val svBind by lazy { ViewDefaultLoadingBinding.inflate(LayoutInflater.from(context), this, false) }

    init {
        addView(svBind.root, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        context.withStyledAttributes(attrs, R.styleable.PageStateLayout) {
            svBind.img.isVisible = getBoolean(R.styleable.PageStateLayout_showImg, true)
        }
    }

    fun showImg(showImg: Boolean) {
        svBind.img.isVisible = showImg
    }

    fun showLoading(showImg: Boolean = true) {
        showState()
        svBind.progress.visible()
        svBind.img.isVisible = showImg
        if (showImg) svBind.img.setImageResource(R.mipmap.img_loading)
        svBind.btn.gone()
        setTitle("加载中…")
    }

    fun showEmpty(showImg: Boolean = true) {
        showState()
        svBind.progress.gone()
        svBind.img.isVisible = showImg
        if (showImg) svBind.img.setImageResource(R.mipmap.img_no_data)
        setTitle("暂无数据")
    }

    fun showContent() {
        svBind.root.gone()
    }

    fun showError(msg: String? = null, showImg: Boolean = true) {
        showState()
        svBind.progress.gone()
        svBind.img.isVisible = showImg
        if (showImg) svBind.img.setImageResource(R.mipmap.img_no_data)
        setTitle(if (msg.isNullOrEmpty()) "加载失败，请稍后再试" else msg)
    }

    private fun showState() {
        svBind.root.visible()
        svBind.root.bringToFront()
    }

    private fun setTitle(text: String) {
        (svBind.title).text = text
    }
}
