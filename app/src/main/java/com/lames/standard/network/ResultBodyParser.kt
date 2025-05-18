package com.lames.standard.network

import com.lames.standard.common.Constants
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.io.IOException
import java.lang.reflect.Type

@Parser(name = "ResultBody")
open class ResultBodyParser<T> : TypeParser<ResultBodyData<T>> {

    protected constructor() : super()
    constructor(type: Type) : super(type)

    @Throws(Exception::class)
    override fun onParse(response: Response): ResultBodyData<T> {
        //val type: Type = response.convert(ResultBodyData::class.java)
        val resultBody: ResultBodyData<T> = response.convertTo(ResultBodyData::class.java, *types)
        when (resultBody.code) {
            Constants.Network.SUCCESS -> return resultBody
            Constants.Network.TOKEN_EXPIRED -> throw SessionExpiredException()
            else -> throw FailRequestException(resultBody.code, resultBody.msg)
        }
    }
}