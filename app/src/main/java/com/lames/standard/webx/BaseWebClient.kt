package com.lames.standard.webx

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.net.http.SslError
import android.webkit.MimeTypeMap
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.net.toUri
import androidx.fragment.app.findFragment
import com.lames.standard.common.GlobalVar
import com.lames.standard.image.ImageKit
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.showErrorToast
import kotlinx.coroutines.runBlocking
import okio.ByteString.Companion.encodeUtf8
import rxhttp.toDownloadAwait
import rxhttp.wrapper.param.RxHttp
import java.io.File

class BaseWebViewClient : WebViewClient() {

    /**
     * 证书校验错误
     */
    @SuppressLint("WebViewClientOnReceivedSslError")
    override fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler,
        error: SslError,
    ) {
        AlertDialog.Builder(view.context)
            .setTitle("提示")
            .setMessage("当前网站安全证书已过期或不可信\n是否继续浏览?")
            .setPositiveButton("继续浏览") { dialog, which ->
                dialog?.dismiss()
                handler.proceed()
            }
            .setNegativeButton("返回上一页") { dialog, which ->
                dialog?.dismiss()
                handler.cancel()
            }
            .create()
            .show()
    }

    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest,
        error: WebResourceError,
    ) {
        if (request.isForMainFrame) {
            onReceivedError(
                view,
                error.errorCode,
                error.description.toString(),
                request.url.toString()
            )
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView,
        request: WebResourceRequest,
    ): Boolean {
        val url = request.url?.toString() ?: return super.shouldOverrideUrlLoading(view, request)
        LogKit.d(javaClass.simpleName, url)
        val fg = view.findFragment<AbsWebViewFragment<*>>()
        val aty = fg.requireActivity()
        when {
            //唤起微信
            url.startsWith("weixin://") -> runCatching {
                aty.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        url.toUri()
                    )
                )
            }
                .onFailure { showErrorToast("该手机没有安装微信") }
            //微信支付响应
            url.contains("wx.tenpay.com") -> view.loadUrl(
                url,
                hashMapOf("Referer" to "微信支付对应的域名")
            )
            //唤起支付宝
            url.startsWith("alipay") -> runCatching {
                aty.startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        url.toUri()
                    )
                )
            }
                .onFailure { showErrorToast("该手机没有支付宝") }

            else -> return false
        }
        return true
    }

    @Deprecated("Deprecated in Java")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        val scheme = Uri.parse(url).scheme ?: return false
        when (scheme) {
            "http", "https" -> view.loadUrl(url)
            // 处理其他协议
            //"tel" ->  {}
        }
        return true
    }

    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest,
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)

        /**
         * 暂不启用，收益感知甚少
         */
        /*var webResourceResponse: WebResourceResponse? = null

        // 如果是 assets 目录下的文件
        if (isAssetsResource(request)) {
            //LogKit.d(javaClass.simpleName, "是 assets 目录下的文件")
            webResourceResponse = assetsResourceRequest(view.context, request)
        }

        // 如果是可以缓存的文件
        if (isCacheResource(request)) {
            //LogKit.d(javaClass.simpleName, "是可以缓存的文件")
            webResourceResponse = cacheResourceRequest(view.context, request)
        }

        if (webResourceResponse == null) {
            //LogKit.d(javaClass.simpleName, "null")
            webResourceResponse = super.shouldInterceptRequest(view, request)
        }
        return webResourceResponse*/
    }

    private fun isAssetsResource(webRequest: WebResourceRequest): Boolean {
        val url = webRequest.url.toString()
        return url.startsWith("file:///android_asset/")
    }

    /**
     * assets 文件请求
     */
    private fun assetsResourceRequest(
        context: Context,
        webRequest: WebResourceRequest,
    ): WebResourceResponse? {
        val url = webRequest.url.toString()
        try {
            val filenameIndex = url.lastIndexOf("/") + 1
            val filename = url.substring(filenameIndex)
            val suffixIndex = url.lastIndexOf(".")
            val suffix = url.substring(suffixIndex + 1)
            val webResourceResponse = WebResourceResponse(
                getMimeTypeFromUrl(url),
                "UTF-8",
                context.assets.open("$suffix/$filename")
            )
            webResourceResponse.responseHeaders = mapOf("access-control-allow-origin" to "*")
            return webResourceResponse
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * 判断是否是可以被缓存等资源
     */
    private fun isCacheResource(webRequest: WebResourceRequest): Boolean {
        val url = webRequest.url.toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return extension == "ico" || extension == "bmp" || extension == "gif"
                || extension == "jpeg" || extension == "jpg" || extension == "png"
                || extension == "svg" || extension == "webp" || extension == "css"
                || extension == "js" || extension == "json" || extension == "eot"
                || extension == "otf" || extension == "ttf" || extension == "woff"
    }

    /**
     * 可缓存文件请求
     */
    private fun cacheResourceRequest(
        context: Context,
        webRequest: WebResourceRequest,
    ): WebResourceResponse? {
        var url = webRequest.url.toString()
        var mimeType = getMimeTypeFromUrl(url)

        // WebView 中的图片利用 Glide 加载(能够和App其他页面共用缓存)
        if (isImageResource(webRequest)) {
            //LogKit.d(javaClass.simpleName, "是图片，开始下载")
            return try {
                val file = ImageKit.with(context).download(url).submit().get()
                //LogKit.d(javaClass.simpleName, "图片下载完毕")
                val webResourceResponse = WebResourceResponse(mimeType, "UTF-8", file.inputStream())
                webResourceResponse.responseHeaders = mapOf("access-control-allow-origin" to "*")
                webResourceResponse
            } catch (e: Exception) {
                LogKit.w(javaClass.simpleName, "图片下载出错${e.message}")
                e.printStackTrace()
                null
            }
        }

        /**
         * 其他文件缓存逻辑
         * 1.寻找缓存文件，本地有缓存直接返回缓存文件
         * 2.无缓存，下载到本地后返回
         * 注意！！！
         * 一定要确保文件下载完整，我这里采用下载完成后给文件加 "success-" 前缀的方法
         */
        val webCachePath = GlobalVar.obtain().webViewPath
        val cacheFilePath =
            webCachePath + File.separator + "success-" + url.encodeUtf8().md5().hex() // 自定义文件命名规则
        val cacheFile = File(cacheFilePath)
        if (!cacheFile.exists() || !cacheFile.isFile) { // 本地不存在 则开始下载
            LogKit.d(javaClass.simpleName, "本地不存在 开始下载")
            // 下载文件
            val sourceFilePath = webCachePath + File.separator + url.encodeUtf8().md5().hex()
            val sourceFile = File(sourceFilePath)
            runBlocking {
                try {
                    RxHttp.get(url, webRequest.requestHeaders).toDownloadAwait(sourceFilePath)
                        .await()
                    // 下载完成后增加 "success-" 前缀 代表文件无损 【防止io流被异常中断导致文件损坏 无法判断】
                    sourceFile.renameTo(cacheFile)
                    //LogKit.d(javaClass.simpleName, "下载成功")
                } catch (e: Exception) {
                    LogKit.w(javaClass.simpleName, "下载失败${e.message}")
                    e.printStackTrace()
                    // 发生异常删除文件
                    sourceFile.deleteOnExit()
                    cacheFile.deleteOnExit()
                }
            }
        }

        // 缓存文件存在则返回
        if (cacheFile.exists() && cacheFile.isFile) {
            //LogKit.d(javaClass.simpleName, "本地存在 开始返回")
            val webResourceResponse =
                WebResourceResponse(mimeType, "UTF-8", cacheFile.inputStream())
            webResourceResponse.responseHeaders = mapOf("access-control-allow-origin" to "*")
            return webResourceResponse
        }
        LogKit.w(javaClass.simpleName, "本地不存在，下载也失败")
        return null
    }

    /**
     * 判断是否是图片
     * 有些文件存储没有后缀，也可以根据自家服务器域名等等
     */
    private fun isImageResource(webRequest: WebResourceRequest): Boolean {
        val url = webRequest.url.toString()
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        return extension == "ico" || extension == "bmp" || extension == "gif"
                || extension == "jpeg" || extension == "jpg" || extension == "png"
                || extension == "svg" || extension == "webp"
    }

    /**
     * 根据 url 获取文件类型
     */
    private fun getMimeTypeFromUrl(url: String): String {
        try {
            val extension = MimeTypeMap.getFileExtensionFromUrl(url)
            if (extension.isNotBlank() && extension != "null") {
                if (extension == "json") {
                    return "application/json"
                }
                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension) ?: "*/*"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "*/*"
    }

}