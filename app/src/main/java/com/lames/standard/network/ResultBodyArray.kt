package com.lames.standard.network

import com.lames.standard.common.Constants

class ResultBodyArray<T> {
    var code: Int = Constants.Network.SUCCESS
    var msg: String? = null
    var `data`: List<T>? = null
}