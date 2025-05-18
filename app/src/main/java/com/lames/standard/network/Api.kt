package com.lames.standard.network


import com.lames.standard.BuildConfig
import rxhttp.wrapper.annotation.DefaultDomain

class Api {

    object Url {
        @JvmField
        @DefaultDomain
        var BASE_URL = BuildConfig.BASE_URL
//        var BASE_URL = ""
//        var BASE_URL = ""
    }

    object H5 {
        //        var H5_URL = ""
//        var H5_URL = ""
//        var H5_URL = ""
        var H5_URL = BuildConfig.URL_DOMAIN
    }

    object H5Page {

    }

    object Event {
        const val UPLOAD_EVENTS = "" //上报事件记录
    }


    object Oss {
        const val GET_WRITE_TOKEN = ""                              //写入oss token
    }


    object Audio {
        const val TO_TEXT = ""//语音转文字
    }

    object Person {
        const val LOGIN = ""                                               //登录接口
        const val SEND_AUTH_CODE = ""                                //发送手机验证码
        const val UPDATE_PERSONAL_INFO = ""                           //更新个人健康信息
        const val GET_BASIC_BODY_INFO = ""                               //获取个人身体指标信息
        const val RESET_PASSWORD = ""                                   //重置密码
    }


}