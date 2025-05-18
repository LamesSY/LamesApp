package com.lames.standard.webx

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import com.lames.standard.R
import com.lames.standard.common.Constants
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.databinding.FragmentWebViewXBinding
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.tools.forColor

class WebViewXFragment : AbsWebViewFragment<FragmentWebViewXBinding>() {

    private lateinit var callback: OnBackPressedCallback
    private val mWebView by lazy { WebViewPool.getInstance().getWebView(requireContext()) }
    private val url by lazy { arguments?.getString(Constants.Params.ARG1) ?: EMPTY_STR }
    private val title by lazy { arguments?.getString(Constants.Params.ARG2) ?: EMPTY_STR }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentWebViewXBinding {
        return FragmentWebViewXBinding.inflate(inflater, container, false)
    }

    override fun initialization() {
        setAppBarTitle(title)
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
            mWebView, RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        )
    }

    override fun doExtra() {
        requireActivity().runOnUiThread { mWebView.loadUrl(url) }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().window.statusBarColor = forColor(R.color.md_theme_primary)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        callback.remove()
    }


}