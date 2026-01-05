package com.lames.standard.wxapi

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.databinding.ActivityContainerBinding
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.network.errorMsg
import com.lames.standard.network.execute
import com.lames.standard.tools.showErrorToast
import com.lames.standard.wxapi.WeChat.iwxApi
import com.tencent.mm.opensdk.modelbase.BaseReq
import com.tencent.mm.opensdk.modelbase.BaseResp
import com.tencent.mm.opensdk.modelmsg.SendAuth
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler

class WXEntryActivity : CommonActivity<ActivityContainerBinding>(), IWXAPIEventHandler {

    override fun getViewBinding() = ActivityContainerBinding.inflate(layoutInflater)

    override fun initialization() {
        try {
            val handleIntent = iwxApi.handleIntent(intent, this)
            if (handleIntent.not()) finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        /*onBackPressedDispatcher.addCallback {

        }*/
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        iwxApi.handleIntent(intent, this)
    }

    //接收微信发送的请求
    override fun onReq(p0: BaseReq?) {

    }

    //接收发送到微信的请求的响应结果
    override fun onResp(p0: BaseResp?) {
        val isBindAction = UserMMKV.user != null
        when (p0?.errCode) {
            BaseResp.ErrCode.ERR_OK -> {
                val code = (p0 as SendAuth.Resp).code
                if (isBindAction) bindWeChatInfo(code) else loginOrRegister(code)
            }

            BaseResp.ErrCode.ERR_USER_CANCEL -> {
                //showErrorToast(if (isBindAction) R.string.bind_canceled else R.string.login_canceled)
                finish()
            }

            BaseResp.ErrCode.ERR_AUTH_DENIED -> {
                //showErrorToast(if (isBindAction) R.string.wechat_bind_denied else R.string.wechat_login_denied)
                finish()
            }

            else -> {
                //showErrorToast(if (isBindAction) R.string.bind_failure else R.string.login_failure)
                finish()
            }
        }
    }

    private fun bindWeChatInfo(wxCode: String) {
        lifecycleScope.execute({
            /*val userWXInfo = getRequest(Api.Person.GET_WX_INFO, hashMapOf("code" to wxCode))
                .removeAllHeader("authorization")
                .toAwaitResultData<UserWXInfo>()
                .await()
            val params = hashMapOf<String, Any?>()
            params["mainUserId"] = UserMMKV.user?.uid
            params["wxOpenId"] = userWXInfo.openId
            params["unionid"] = userWXInfo.unionid
            val bindResult = postForData<Boolean>(Api.Person.BIND_WECHAT, params)
            if (bindResult) showSuccessToast(R.string.bind_wechat_success)
            else showErrorToast(R.string.bind_wechat_fail)
            finish()*/
        }, {
            showErrorToast(it.errorMsg)
            finish()
        }, { showProgressDialog(R.string.operating) }, { dismissProgressDialog() })
    }

    private fun loginOrRegister(wxCode: String) {
        lifecycleScope.execute({
            /*val userWXInfo = getData<UserWXInfo>(Api.Person.GET_WX_INFO, hashMapOf("code" to wxCode))
            val params = hashMapOf<String, Any?>()
            params["wxCode"] = wxCode
            params["loginType"] = 3
            params["wxOpenId"] = userWXInfo.openId
            params["unionid"] = userWXInfo.unionid
            if (userWXInfo.register.not()) {
                AddWXPhoneNumActivity.start(this@WXEntryActivity, userWXInfo)
                finish()
                return@execute
            }
            val user = postForData<User>(Api.Person.LOGIN, params)
            UserMMKV.user = user
            delay(1000)
            val nextAty = when {
                user.uid.isEmpty() -> LoginActivity::class.java
                user.isInfoFinish.not() -> UserInitActivity::class.java
                else -> HomeActivity::class.java
            }
            val intent = Intent(this@WXEntryActivity, nextAty)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            Activity.startActivity(intent)*/
        }, {
            showErrorToast(it.errorMsg)
            onBackPressedDispatcher.onBackPressed()
        }, { }, { dismissProgressDialog() })

    }
}