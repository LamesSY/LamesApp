package com.lames.standard.webx

import android.content.Context
import android.content.MutableContextWrapper
import com.lames.standard.tools.LogKit
import java.util.Stack

class WebViewPool private constructor() {

    companion object {

        private const val TAG = "WebViewPool"

        @Volatile
        private var instance: WebViewPool? = null

        fun getInstance(): WebViewPool {
            return instance ?: synchronized(this) {
                instance ?: WebViewPool().also { instance = it }
            }
        }
    }

    private val sPool = Stack<BaseWebView>()
    private val lock = byteArrayOf()
    private var maxSize = 1

    /**
     * 设置 webview 池容量
     */
    fun setMaxPoolSize(size: Int) {
        synchronized(lock) { maxSize = size }
    }

    /**
     * 初始化webview 放在list中
     */
    fun init(context: Context, initSize: Int = maxSize) {
        for (i in 0 until initSize) {
            val view = BaseWebView(MutableContextWrapper(context))
            view.webChromeClient = BaseWebChromeClient()
            view.webViewClient = BaseWebViewClient()
            sPool.push(view)
        }
    }

    /**
     * 获取webview
     */
    fun getWebView(context: Context): BaseWebView {
        synchronized(lock) {
            val webView: BaseWebView
            if (sPool.size > 0) {
                webView = sPool.pop()
                LogKit.d(TAG, "getWebView from pool")
            } else {
                webView = BaseWebView(MutableContextWrapper(context))
                LogKit.d(TAG, "getWebView from create")
            }

            (webView.context as MutableContextWrapper).baseContext = context
            //val contextWrapper = webView.context as MutableContextWrapper
            //contextWrapper.baseContext = context

            // 默认设置
            webView.webChromeClient = BaseWebChromeClient()
            webView.webViewClient = BaseWebViewClient()
            return webView
        }
    }

    /**
     * 回收 WebView
     */
    fun recycle(webView: BaseWebView) {
        // 释放资源
        webView.release()

        // 根据池容量判断是否销毁 【也可以增加其他条件 如手机低内存等等】
        val contextWrapper = webView.context as MutableContextWrapper
        contextWrapper.baseContext = webView.context.applicationContext
        synchronized(lock) {
            if (sPool.size < maxSize) {
                sPool.push(webView)
            } else {
                webView.destroy()
            }
        }
    }
}