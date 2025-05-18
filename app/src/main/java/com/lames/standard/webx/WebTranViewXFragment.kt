package com.lames.standard.webx

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import com.lames.standard.R
import com.lames.standard.common.Constants
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.databinding.FragmentWebTranViewXBinding
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.tools.dp2px
import com.lames.standard.tools.forColor
import com.lames.standard.tools.getStatusBarHeight

class WebTranViewXFragment : AbsWebViewFragment<FragmentWebTranViewXBinding>() {

    private lateinit var callback: OnBackPressedCallback
    private val mWebView by lazy { WebViewPool.getInstance().getWebView(requireContext()) }
    private val url by lazy { arguments?.getString(Constants.Params.ARG1) ?: EMPTY_STR }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWebTranViewXBinding {
        return FragmentWebTranViewXBinding.inflate(inflater, container, false)
    }

    override fun initialization() {
        callback = dispatcher.addCallback(this) {
            if (mWebView.canGoBack()) mWebView.goBack()
            else {
                callback.isEnabled = false
                dispatcher.onBackPressed()
            }
        }
        mWebView.setLifecycleOwner(this)
        mWebView.settings.textZoom = AppConfigMMKV.webViewTextZoom
        binding.webViewContainer.addView(
            mWebView, 0, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )
        val barPadding = requireContext().dp2px(10)
        binding.appBar.setPadding(0, getStatusBarHeight() + barPadding, 0, barPadding)
        mWebView.setOnScrollChangeListener { view, i, i2, i3, i4 ->
            val fc = forColor(if (i2 == 0) R.color.text_1 else R.color.text_1_rev)
            val bc = forColor(if (i2 == 0) R.color.transparent else R.color.md_theme_primary)
            binding.appBack.imageTintList = ColorStateList.valueOf(fc)
            binding.appTitle.setTextColor(fc)
            binding.appBar.setBackgroundColor(bc)
        }
    }

    override fun doExtra() {
        requireActivity().runOnUiThread { mWebView.loadUrl(url) }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = forColor(R.color.transparent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.remove()
    }
}