package com.lames.standard.webx

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.lames.standard.BuildConfig
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.network.Api
import com.lames.standard.tools.LogKit
import kotlin.math.abs

open class BaseWebView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
) : WebView(context, attrs), LifecycleEventObserver {

    init {
        // WebView 调试模式开关
        setWebContentsDebuggingEnabled(BuildConfig.DEBUG)
        // 不显示滚动条
        isVerticalScrollBarEnabled = false
        isHorizontalScrollBarEnabled = false
        // 初始化设置
        this.settings.apply {
            allowFileAccess = true
            javaScriptEnabled = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_DEFAULT
            layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL
            useWideViewPort = true
            loadWithOverviewMode = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            setGeolocationEnabled(true)
            setSupportZoom(false)
            textZoom = AppConfigMMKV.webViewTextZoom
        }
    }

    fun addJsInterface(webViewJsBridge: WebViewJsBridge) {
        this.addJavascriptInterface(webViewJsBridge, "basic")
    }

    fun loadRefresh(url: String) {
        if (this.url?.endsWith(url) == true) this.reload() else loadUrl(url)
    }

    override fun loadUrl(url: String) {
        val pres = listOf("http", "about", "javascript")
        val urlX = if (pres.any { url.startsWith(it) }) url
        else "${Api.H5.H5_URL}$url"
        LogKit.d(javaClass.simpleName, "loadUrl($urlX)")
        super.loadUrl(urlX)
    }

    /**
     * 获取当前url
     */
    override fun getUrl(): String? {
        return super.getOriginalUrl() ?: return super.getUrl()
    }

    override fun canGoBack(): Boolean {
        val backForwardList = copyBackForwardList()
        val currentIndex = backForwardList.currentIndex - 1
        if (currentIndex >= 0) {
            val item = backForwardList.getItemAtIndex(currentIndex)
            if (item?.url == "about:blank") {
                return false
            }
        }
        return super.canGoBack()
    }

    var touchActionType = 0
    private var startY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) return super.onTouchEvent(null)

        if (touchActionType == 1) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startY = event.y // 记录触摸起点
                    return super.onTouchEvent(event) // 允许点击事件传递
                }

                MotionEvent.ACTION_MOVE -> {
                    // 检测到垂直滑动时，禁止事件传递
                    val deltaY = abs(event.y - startY)
                    if (deltaY > touchSlop) return false
                }
            }
        }

        return super.onTouchEvent(event)

    }

    /**
     * 设置 WebView 生命管控（自动回调生命周期方法）
     */
    fun setLifecycleOwner(owner: LifecycleOwner) {
        owner.lifecycle.addObserver(this)
    }

    /**
     * 生命周期回调
     */
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_DESTROY -> {
                source.lifecycle.removeObserver(this)
                onDestroy()
            }

            else -> {}
        }
    }

    /**
     * 生命周期 onResume()
     */
    @SuppressLint("SetJavaScriptEnabled")
    override fun onResume() {
        //LogKit.v(javaClass.simpleName, "$url")
        super.onResume()
        settings.javaScriptEnabled = true
    }

    /**
     * 生命周期 onPause()
     */
    override fun onPause() {
        //LogKit.v(javaClass.simpleName, "$url")
        super.onPause()
    }

    /**
     * 生命周期 onDestroy()
     * 父类没有 需要自己写
     */
    fun onDestroy() {
        //LogKit.v(javaClass.simpleName, "$url")
        settings.javaScriptEnabled = false
        //WebViewPool.getInstance().recycle(this)
    }

    /**
     * 释放资源操作
     */
    fun release() {
        (parent as ViewGroup?)?.removeView(this)
        //removeAllViews()
        stopLoading()
        setCustomWebViewClient(null)
        setCustomWebChromeClient(null)
        setDownloadListener(null)
        loadUrl("about:blank")
        clearHistory()
    }

    fun setCustomWebViewClient(client: BaseWebViewClient?) {
        if (client == null) {
            super.setWebViewClient(WebViewClient())
        } else {
            super.setWebViewClient(client)
        }
    }

    fun setCustomWebChromeClient(client: BaseWebChromeClient?) {
        super.setWebChromeClient(client)
    }

    fun evaluateJs(funName: String, msg: String) {
        LogKit.d(javaClass.simpleName, "evaluateJs funName=$funName msg=$msg")
        evaluateJavascript("javascript:${funName}('${msg}')") {}
    }
}
