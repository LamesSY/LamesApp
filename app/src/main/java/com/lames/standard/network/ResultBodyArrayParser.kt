package com.lames.standard.network

import com.lames.standard.common.Constants
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.io.IOException
import java.lang.reflect.Type


@Parser(name = "ResultArray")
open class ResultBodyArrayParser<T> : TypeParser<List<T>> {

    protected constructor() : super()
    constructor(type: Type) : super(type)

    @Throws(IOException::class)
    override fun onParse(response: Response): List<T> {
        val result: ResultBodyArray<T> = response.convertTo(ResultBodyArray::class.java, *types)
        val list = result.data
        when (result.code) {
            Constants.Network.SUCCESS -> return list ?: arrayListOf()
            Constants.Network.TOKEN_EXPIRED -> throw SessionExpiredException()
            else -> throw FailRequestException(result.code, result.msg)
        }
    }
}