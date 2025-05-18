package com.lames.standard.push

import android.app.Application
import android.content.Context
import com.alibaba.sdk.android.push.CloudPushService
import com.alibaba.sdk.android.push.CommonCallback
import com.alibaba.sdk.android.push.noonesdk.PushInitConfig
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory
import com.lames.standard.BuildConfig
import com.lames.standard.tools.LogKit

/**
 * ：https://help.aliyun.com/document_detail/434669.html?spm=a2c4g.434660.0.0.49033fd06Z2p5T
 */
object PushHelp {

    private val TAG = this::class.java.simpleName

    /**
     * 初始化操作等
     */
    fun initPush(app: Application) {
        val pushInitConfig = PushInitConfig.Builder()
            .application(app)
            .appKey("335488663")
            .appSecret("13398fc79ed44529a424ca1f270e8aa5")
            .build()
        PushServiceFactory.init(pushInitConfig)
        if (BuildConfig.DEBUG) {
            //日志等级
            PushServiceFactory.getCloudPushService().setLogLevel(CloudPushService.LOG_DEBUG)
        } else {
            //日志等级,生产关闭
            PushServiceFactory.getCloudPushService().setLogLevel(CloudPushService.LOG_OFF)
        }
    }

    /**
     * 绑定账号
     */
    fun bindAccount(context: Context, uid: String?) {
        //建立推送的长连接
        val pushService = PushServiceFactory.getCloudPushService()
        pushService.register(context, object : CommonCallback {
            override fun onSuccess(success: String) {
                LogKit.d(TAG, "推送push register成功 success:$success")
                //设备标识符
                val deviceId = PushServiceFactory.getCloudPushService().deviceId
                LogKit.d(TAG, "推送push deviceId:$deviceId", true)
                //设置接收消息的service
                pushService.setPushIntentService(PushMsgService::class.java)
                if (uid.isNullOrEmpty()) {
                    LogKit.e(TAG, "推送push 绑定账号 uid为空", true)
                    return
                }
                LogKit.d(TAG, "推送push 绑定账号 uid:$uid")
                pushService.bindAccount(uid, object : CommonCallback {
                    override fun onSuccess(s: String?) {
                        LogKit.d(TAG, "推送push 绑定账号 success:$s", true)
                    }

                    override fun onFailed(errorCode: String?, errorMsg: String?) {
                        LogKit.d(
                            TAG,
                            "推送push 绑定账号 onFailed errorCode:$errorCode errorMsg:$errorMsg",
                            true
                        )
                    }
                })
            }

            override fun onFailed(errorCode: String, errorMessage: String) {
                LogKit.e(
                    TAG,
                    "推送push register失败 errorCode:${errorCode},errorMessage:${errorMessage}",
                    true
                )
            }
        })
    }

    /**
     * 解绑账号
     */
    fun unbindAccount(uid: String?) {
        if (uid.isNullOrEmpty()) {
            LogKit.e(TAG, "推送push 解绑账号 uid为空", true)
            return
        }
        LogKit.d(TAG, "推送push 解绑账号 uid:$uid")
        PushServiceFactory.getCloudPushService().unbindAccount(object : CommonCallback {
            override fun onSuccess(s: String?) {
                LogKit.d(TAG, "推送push 解绑账号 success:$s", true)
            }

            override fun onFailed(errorCode: String?, errorMsg: String?) {
                LogKit.d(
                    TAG,
                    "推送push 解绑账号 onFailed errorCode:$errorCode errorMsg:$errorMsg",
                    true
                )
            }
        })
    }

}