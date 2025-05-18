package com.lames.standard.entity

import android.os.Build
import com.lames.standard.BuildConfig
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.network.HttpKit
import com.lames.standard.tools.toPatternString


class LogEvent {

    companion object {
        fun newEvent(): LogEvent = LogEvent().apply {
            this.platform = -1
            this.os_type = "android"
            this.os_version = Build.VERSION.RELEASE
            this.country_code = HttpKit.currentCountry
            this.language_code = HttpKit.currentLanguage
            this.mobile_brand = Build.BRAND.uppercase()
            this.mobile_model = Build.MODEL
            this.app_version = BuildConfig.VERSION_NAME + ".${BuildConfig.VERSION_CODE}"
            this.user_id = UserMMKV.user?.uid
            this.visitor_id = AppConfigMMKV.getAndroidId()
            this.op_time = System.currentTimeMillis().toPatternString("yyyy-MM-dd HH:mm:ss")
            this.op_time_zone = "+8"
            this.active = if (BuildConfig.DEBUG) "test" else "prod"
        }
    }

    var platform: Short? = null                     //平台区分
    var os_type: String? = null                     //入库时间（api拿到的北京时间）
    var os_version: String? = null                  //操作系统ios/android/h5/watch
    var country_code: String? = null                //操作系统版本
    var language_code: String? = null               //国家
    var currency_code: String? = null               //语言
    var mobile_brand: String? = null                //货币
    var mobile_model: String? = null                //手机品牌
    var app_version: String? = null                 //app版本号
    var user_id: String? = null                     //app版本号
    var visitor_id: String? = null                  //用户id
    var op_time: String? = null                     //配对设备唯一id
    var op_time_zone: String? = null                //操作时间,client时区时间
    var op_location: String? = null                 //client时区
    var active: String? = null                      //区分生产/测试环境

    var op_type: String? = null                     //事件名称
    var opvs1: String? = null                       //事件String参数1
    var opvs2: String? = null                       //事件String参数2
    var opvs3: String? = null                       //事件String参数3
    var opvs4: String? = null                       //事件String参数4
    var opvs5: String? = null                       //事件String参数5
    var opvs6: String? = null                       //事件String参数6
    var opvl1: Long? = null                         //事件Long参数1
    var opvl2: Long? = null                         //事件Long参数2
    var opvl3: Long? = null                         //事件Long参数3
    var opvl4: Long? = null                         //事件Long参数4
    var opvl5: Long? = null                         //事件Long参数5
    var opvd1: Double? = null                       //事件Double参数1
    var opvd2: Double? = null                       //事件Double参数2
    var opvd3: Double? = null                       //事件Double参数3
    var opvd4: Double? = null                       //事件Double参数4
    var opvd5: Double? = null                       //事件Double参数5
}