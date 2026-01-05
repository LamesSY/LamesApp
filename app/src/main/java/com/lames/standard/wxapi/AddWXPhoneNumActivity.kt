package com.lames.standard.wxapi

import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityAddWxPhoneNumBinding
import com.lames.standard.entity.User
import com.lames.standard.entity.UserWXInfo
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.network.Api
import com.lames.standard.network.errorMsg
import com.lames.standard.network.execute
import com.lames.standard.network.postForData
import com.lames.standard.network.postRequest
import com.lames.standard.tools.forString
import com.lames.standard.tools.isPhone
import com.lames.standard.tools.isRunning
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast
import com.lames.standard.tools.showSuccessToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import rxhttp.tryAwait
import rxhttp.wrapper.param.toAwaitResultBody

class AddWXPhoneNumActivity : CommonActivity<ActivityAddWxPhoneNumBinding>() {

    private var getAuthCodeJob: Job? = null
    private val userWXInfo by lazy { intent.getParcelableExtra<UserWXInfo>(Constants.Params.ARG1)!! }

    override fun getViewBinding() = ActivityAddWxPhoneNumBinding.inflate(layoutInflater)

    override fun bindEvent() {
        binding.getAuthCode.onClick {
            sendAuthCode()
        }

        binding.nextStep.onClick {
            if (binding.phoneInput.text.isNullOrEmpty()) {
                showErrorToast(R.string.plz_input_phone_num)
                return@onClick
            }
            if (binding.authCodeInput.text.isNullOrEmpty()) {
                showErrorToast(R.string.plz_input_auth_code)
                return@onClick
            }
            lifecycleScope.execute({
                val params = hashMapOf<String, Any?>()
                params["loginType"] = 2
                params["phone"] = binding.phoneInput.text.toString()
                params["smsVerifyCode"] = binding.authCodeInput.text.toString()
                params["nickname"] = userWXInfo.nickName
                params["avatar"] = userWXInfo.avatar
                params["userName"] = userWXInfo.nickName
                params["unionid"] = userWXInfo.unionid
                params["wxOpenId"] = userWXInfo.openId
                val user = postForData<User>(Api.Person.REGISTER, params)
                UserMMKV.user = user
                /*if (user.isInfoFinish.not()) {
                    UserInitActivity.start(this@AddWXPhoneNumActivity)
                } else {
                    HomeActivity.start(this@AddWXPhoneNumActivity)
                }*/
                finish()
            }, null, { showProgressDialog(R.string.operating) }, { dismissProgressDialog() })
        }
    }

    private fun sendAuthCode() {
        if (binding.phoneInput.text.isNullOrEmpty()) {
            showErrorToast(R.string.plz_input_phone_num)
            return
        }
        if (binding.phoneInput.text.toString().isPhone().not()) {
            showErrorToast(R.string.plz_input_correct_phone)
            return
        }
        if (getAuthCodeJob.isRunning()) return
        getAuthCodeJob = lifecycleScope.execute({
            showProgressDialog(R.string.operating)
            val params = hashMapOf<String, Any?>("phone" to binding.phoneInput.text?.toString())
            params["areaCode"] = binding.phoneInput.text.toString()
            val result = postRequest(Api.Person.SEND_AUTH_CODE, params)
                .removeAllHeader("authorization")
                .toAwaitResultBody<Any>()
                .tryAwait { showErrorToast(it.errorMsg) }
            dismissProgressDialog()
            if (result?.code != 200) return@execute

            showSuccessToast(R.string.auth_code_send)
            repeat(60) {
                binding.getAuthCode.text = "${60 - it}${forString(R.string.get_again_after_second)}"
                delay(1000)
            }
            binding.getAuthCode.setText(R.string.get_auth_code)
        })
    }

    companion object {
        fun start(context: Context, userWXInfo: UserWXInfo) {
            val intent = Intent(context, AddWXPhoneNumActivity::class.java)
            intent.putExtra(Constants.Params.ARG1, userWXInfo)
            context.startActivity(intent)
        }
    }
}