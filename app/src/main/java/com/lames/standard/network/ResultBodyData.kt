package com.lames.standard.network

import com.lames.standard.common.Constants

class ResultBodyData<T> {
    var code: Int = Constants.Network.SUCCESS
    var msg: String? = null
    var `data`: T? = null
}