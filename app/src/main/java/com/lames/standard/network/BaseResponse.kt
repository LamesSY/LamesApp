package com.lames.standard.network

class BaseResponse<T> {
    var code: Int = 200
    var msg: String? = null
    var `data`: T? = null
}