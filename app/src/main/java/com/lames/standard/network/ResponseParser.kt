package com.lames.standard.network

import com.lames.standard.common.Constants
import rxhttp.wrapper.annotation.Parser
import rxhttp.wrapper.exception.ParseException
import rxhttp.wrapper.parse.TypeParser
import rxhttp.wrapper.utils.convertTo
import java.io.IOException
import java.lang.reflect.Type

@Parser(name = "Response")
open class ResponseParser<T> : TypeParser<T> {

    //以下两个构造方法是必须的
    protected constructor() : super()
    constructor(type: Type) : super(type)

    @Throws(IOException::class)
    override fun onParse(response: okhttp3.Response): T {
        val data: BaseResponse<T> = response.convertTo(BaseResponse::class, *types)
        val t = data.data     //获取data字段
        if (data.code != Constants.Network.SUCCESS || t == null) { //code不等于200，说明数据不正确，抛出异常
            throw ParseException(data.code.toString(), data.msg, response)
        }
        return t
    }
}