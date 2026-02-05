package com.lames.standard.webx

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import com.lames.standard.R
import com.lames.standard.common.Constants
import com.lames.standard.databinding.FragmentWebViewBinding
import com.lames.standard.entity.WebViewAtyStyle
import com.lames.standard.tools.dp2px
import com.lames.standard.tools.forColor
import com.lames.standard.tools.getStatusBarHeight
import com.lames.standard.view.LmAppBar
import com.lames.standard.webx.WebViewActivity.Companion.BAR_NORMAL
import com.lames.standard.webx.WebViewActivity.Companion.BAR_REMOVE
import com.lames.standard.webx.WebViewActivity.Companion.BAR_TRANSPORT

class WebViewFragment : AbsWebViewFragment<FragmentWebViewBinding>() {

    private lateinit var callback: OnBackPressedCallback
    private val mWebView by lazy { WebViewPool.getInstance().getWebView(requireContext()) }

    private val url by lazy { arguments?.getString(Constants.Params.ARG1) ?: "" }
    private val wvAtyStyle by lazy { arguments?.getParcelable<WebViewAtyStyle>(Constants.Params.ARG2) ?: WebViewAtyStyle() }

    override fun getWebView() = mWebView

    override fun getViewBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentWebViewBinding {
        return FragmentWebViewBinding.inflate(inflater, container, false)
    }

    override fun initialization() {
        setAppBarTitle(wvAtyStyle.title)
        callback = dispatcher.addCallback(this) {
            if (mWebView.canGoBack()) mWebView.goBack()
            else {
                callback.isEnabled = false
                dispatcher.onBackPressed()
            }
        }

        val statusHeight = getStatusBarHeight()

        when (wvAtyStyle.barStyle) {
            BAR_NORMAL -> binding.lmAppBar.setNormalTheme()
            BAR_TRANSPORT -> binding.lmAppBar.setTranTheme()
        }
        binding.lmAppBar.isGone = wvAtyStyle.barStyle == BAR_REMOVE
        if (wvAtyStyle.barStyle != BAR_REMOVE) {
            binding.lmAppBar.updateLayoutParams<ViewGroup.MarginLayoutParams> { topMargin = statusHeight }
        }

        mWebView.addJsInterface(WebViewJsBridge(mWebView, this))
        mWebView.setLifecycleOwner(this)
        val lp = RelativeLayout.LayoutParams(
            RelativeLayout.LayoutParams.MATCH_PARENT,
            RelativeLayout.LayoutParams.MATCH_PARENT
        )
        if (wvAtyStyle.barStyle == BAR_NORMAL) lp.topMargin = statusHeight + requireContext().dp2px(LmAppBar.HEIGHT_DP)
        binding.webViewContainer.addView(mWebView, lp)

        if (wvAtyStyle.barStyle == BAR_TRANSPORT) {
            binding.lmAppBar.setTranTheme()
            mWebView.viewTreeObserver.takeIf { it.isAlive }?.addOnScrollChangedListener {
                if (mWebView.scrollY > 200) {
                    binding.lmAppBar.setNormalTheme()
                    requireActivity().window.statusBarColor = forColor(R.color.windowBg_1)
                } else {
                    binding.lmAppBar.setTranTheme()
                    requireActivity().window.statusBarColor = 0
                }
            }
        }
    }

    override fun bindEvent() {
        getLmAppBar().onAppCloseIconClick { requireActivity().finish() }
    }

    override fun doExtra() {
        requireActivity().runOnUiThread {
            if (wvAtyStyle.action == 2) {
                mWebView.postUrl(url, wvAtyStyle.extraParams?.toByteArray() ?: byteArrayOf())
            } else mWebView.loadUrl(url)
        }
    }
}