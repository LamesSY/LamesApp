package com.lames.standard.webx

import android.net.Uri
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.fragment.app.findFragment

class BaseWebChromeClient : WebChromeClient() {

    private var webView: WebView? = null

    //用于处理全局播放视频
    private var customView: View? = null
    private var customViewCallback: CustomViewCallback? = null

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        webView = view
        super.onProgressChanged(view, newProgress)
    }

    override fun onShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        val fg = webView?.findFragment<AbsWebViewFragment<*>>() ?: return false
        fg.onAbsShowFileChooser(webView, filePathCallback, fileChooserParams)
        return true
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (customView != null || webView == null) {
            callback?.onCustomViewHidden()
            return
        }

        webView?.addView(
            view,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        customView = view
        customViewCallback = callback
    }

    override fun onHideCustomView() {
        customView ?: return
        webView?.removeView(customView)
        customView = null
        customViewCallback?.onCustomViewHidden()
    }

}