package com.lames.standard.network

import com.lames.standard.common.Constants
import okhttp3.Response
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.lang.reflect.Type

@Parser(name = "ResultData")
open class ResultBodyDataParser<T> : TypeParser<T> {

    protected constructor() : super()
    constructor(type: Type) : super(type)

    @Throws(Exception::class)
    override fun onParse(response: Response): T {
        val data: ResultBodyData<T> = response.convertTo(ResultBodyData::class.java, *types)
        val t = data.data
        when (data.code) {
            Constants.Network.SUCCESS -> return t ?: throw NoResultException()
            Constants.Network.TOKEN_EXPIRED -> throw SessionExpiredException()
            else -> throw FailRequestException(data.code, data.msg)
        }
    }
}