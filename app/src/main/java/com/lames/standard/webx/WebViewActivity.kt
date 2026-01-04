package com.lames.standard.webx

import android.content.Context
import android.content.Intent
import android.widget.RelativeLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.core.view.isVisible
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityWebViewBinding
import com.lames.standard.entity.WebViewAtyStyle
import com.lames.standard.mmkv.AppConfigMMKV

class WebViewActivity : AbsWebViewActivity<ActivityWebViewBinding>() {

    private lateinit var callback: OnBackPressedCallback
    private val mWebView by lazy { WebViewPool.getInstance().getWebView(this) }
    private val url by lazy { intent.getStringExtra(Constants.Params.ARG1) ?: "" }
    private val wvAtyStyle by lazy { intent.getParcelableExtra<WebViewAtyStyle>(Constants.Params.ARG2) ?: WebViewAtyStyle() }

    override fun getViewBinding() = ActivityWebViewBinding.inflate(layoutInflater)

    override fun initialization() {
        setAppBarTitle(wvAtyStyle.title)
        callback = onBackPressedDispatcher.addCallback(this) {
            if (mWebView.canGoBack()) mWebView.goBack()
            else {
                callback.isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.appBarSpace.isVisible = wvAtyStyle.barStyle == 0
        mWebView.addJsInterface(WebViewJsBridge(mWebView, this))
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
        runOnUiThread { mWebView.loadUrl(url) }
    }

    companion object {
        fun start(context: Context, url: String, title: String? = null, barStyle: Int = 0) {
            val webviewStyle = WebViewAtyStyle()
            webviewStyle.title = title ?: ""
            webviewStyle.barStyle = barStyle

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(Constants.Params.ARG1, url)
            intent.putExtra(Constants.Params.ARG2, webviewStyle)
            context.startActivity(intent)
        }
    }

}