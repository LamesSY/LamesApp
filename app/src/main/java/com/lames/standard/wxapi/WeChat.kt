package com.lames.standard.wxapi

import com.lames.standard.R
import com.lames.standard.common.CommonApp
import com.lames.standard.tools.showErrorToast
import com.tencent.mm.opensdk.constants.Build
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram
import com.tencent.mm.opensdk.modelbiz.WXOpenCustomerServiceChat
import com.tencent.mm.opensdk.openapi.IWXAPI
import com.tencent.mm.opensdk.openapi.WXAPIFactory

object WeChat {

    private const val APP_ID = ""

    private val iwxApi by lazy { WXAPIFactory.createWXAPI(CommonApp.obtain(), APP_ID, true) }

    private fun sendReqAfterInstall(block: IWXAPI.() -> Unit) {
        if (!iwxApi.isWXAppInstalled) {
            showErrorToast(R.string.not_installed_wechat)
            return
        }
        block.invoke(iwxApi)
    }

    fun toWeChatOfficial(corpId: String, url: String) = sendReqAfterInstall {
        if (wxAppSupportAPI < Build.SUPPORT_OPEN_CUSTOMER_SERVICE_CHAT) {
            showErrorToast(R.string.not_support_yet)
            return@sendReqAfterInstall
        }
        sendReq(WXOpenCustomerServiceChat.Req().apply {
            this.corpId = corpId
            this.url = url
        })
    }

    fun toWehChatMiniApp(userName: String, path: String, extraData: String? = null) =
        sendReqAfterInstall {
            val req = WXLaunchMiniProgram.Req()
            req.userName = userName
            req.path = path
            req.miniprogramType = WXLaunchMiniProgram.Req.MINIPTOGRAM_TYPE_RELEASE
            req.extData = extraData
            iwxApi.sendReq(req)
        }


}