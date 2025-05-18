package com.lames.standard.network

import com.lames.standard.event.EventDict
import com.lames.standard.event.LogEventKit
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer
import rxhttp.RxHttpPlugins
import rxhttp.wrapper.OkHttpCompat
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.*

class CusLogInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        if (request.url.encodedPath.contains(Api.Event.UPLOAD_EVENTS, true)) return response

        val responseStr =
            runCatching { response2Str(response) }.getOrDefault("No Or Error Response Body")
        LogEventKit.obtain().logEvent(
            EventDict.api_log, mapOf(
                EventDict.opvs1 to "url:${request.url} requestBodyStr: $responseStr",
                EventDict.opvs2 to if (response.code == 200) "200" else "$responseStr",
                EventDict.opvd1 to response.code,
            )
        )
        return response
    }

    @Throws(IOException::class)
    private fun response2Str(response: Response): String? {
        val body = response.body
        val onResultDecoder = OkHttpCompat.needDecodeResult(response)
        val source = body!!.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source.buffer()
        var result: String?
        if (isProbablyUtf8(buffer)) {
            result = getCharset(body)?.let { buffer.clone().readString(it) }
            if (onResultDecoder) {
                result = RxHttpPlugins.onResultDecoder(result)
            }
        } else {
            result = "(binary " + buffer.size + "-byte body omitted)"
        }
        return result
    }

    private fun isProbablyUtf8(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }

    private fun getCharset(responseBody: ResponseBody): Charset? {
        val mediaType = responseBody.contentType()
        return if (mediaType != null) mediaType.charset(UTF_8) else UTF_8
    }
}