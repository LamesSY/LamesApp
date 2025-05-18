package com.lames.standard.network

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.TimeoutCancellationException
import rxhttp.wrapper.exception.HttpStatusCodeException
import rxhttp.wrapper.exception.ParseException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class SessionExpiredException : RuntimeException()

class FailRequestException(val code: Int, reason: String?) : RuntimeException(reason)

class NoResultException : RuntimeException()

val Throwable.errorCode: String
    get() = when (this) {
        //Http状态码异常
        is HttpStatusCodeException -> this.statusCode.toString()
        //业务code异常
        is ParseException -> this.errorCode
        is FailRequestException -> this.code.toString()
        else -> this.javaClass.simpleName
    }

val Throwable.errorMsg: String
    get() = when (this) {
        is UnknownHostException -> "网络连接失败，请检查您的网络设置"
        is TimeoutException -> "服务请求超时，请稍后重试"
        is TimeoutCancellationException -> "您的会话已过期，请重新登录"
        is JsonSyntaxException -> "数据异常，请联系管理员处理"
        is ParseException -> message ?: "系统遇到了未知的错误，请稍后重试或联系管理员"
        else -> message ?: "系统遇到了未知的错误，请稍后重试或联系管理员"
    }
