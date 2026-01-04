package com.lames.standard.module.login

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.databinding.ActivityLoginBinding
import com.lames.standard.dialog.AgreeDialogFragment
import com.lames.standard.entity.User
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.module.home.HomeActivity
import com.lames.standard.network.Api
import com.lames.standard.network.execute
import com.lames.standard.network.postForData
import com.lames.standard.network.postRequest
import com.lames.standard.tools.forColor
import com.lames.standard.tools.forString
import com.lames.standard.tools.isPhone
import com.lames.standard.tools.isRunning
import com.lames.standard.tools.onClick
import com.lames.standard.tools.readTextFileFromAssets
import com.lames.standard.tools.showErrorToast
import com.lames.standard.tools.showSuccessToast
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import rxhttp.tryAwait
import rxhttp.wrapper.param.toAwaitResultBody

class LoginActivity : CommonActivity<ActivityLoginBinding>() {

    //1密码登录 2验证码登录
    private var loginType = 2
    private var agree = false

    private var getAuthCodeJob: Job? = null

    override fun getViewBinding() = ActivityLoginBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        binding.setPwd.text = buildSpannedString {
            append("注册未设置过密码的需去设置密码")
            color(forColor(R.color.label_1)) { append("去设置密码") }
        }
    }

    override fun bindEvent() {
        binding.agreeLayout.onClick {
            agree = agree.not()
            binding.checkImg.setImageResource(if (agree) R.drawable.ic_checked else R.drawable.ic_unchecked)
        }

        binding.protocol.onClick {

        }

        binding.privacy.onClick {
            //WebViewXActivity.showPrivacy(this)
        }
        binding.getAuthCode.onClick {
            if (getAuthCodeJob.isRunning()) return@onClick
            val phone = binding.editAccount.text.toString()
            if (phone.isEmpty() || phone.isPhone().not()) {
                showErrorToast(R.string.plz_input_correct_phone)
                return@onClick
            }
            getAuthCodeJob = lifecycleScope.execute({
                val result = postRequest(
                    Api.Person.SEND_AUTH_CODE,
                    hashMapOf<String, Any?>("phone" to binding.editAccount.text?.toString())
                )
                    .removeAllHeader("authorization")
                    .toAwaitResultBody<Any>()
                    .tryAwait()
                if (result?.code != 200) {
                    showErrorToast(result?.msg ?: "${result?.code}")
                    return@execute
                }
                showSuccessToast(R.string.auth_code_send)
                repeat(60) {
                    binding.getAuthCode.text =
                        "${60 - it}${forString(R.string.get_again_after_second)}"
                    delay(1000)
                }
                binding.getAuthCode.setText(R.string.get_auth_code)
            })
        }

        binding.switchLogin.onClick {
            loginType = if (loginType == 2) 1 else 2
            binding.forgetPwd.isVisible = loginType == 1
            binding.setPwd.isInvisible = loginType == 2
            binding.authCodeLayout.isVisible = loginType == 2
            binding.password.isVisible = loginType == 1
            binding.switchLogin.setText(
                if (loginType == 2) R.string.password_login else R.string.auth_code_login
            )
        }

        binding.setPwd.onClick {
            ForgetPwdActivity.start(this)
        }

        binding.forgetPwd.onClick {
            ForgetPwdActivity.start(this)
        }

        binding.login.onClick {
            val phone = binding.editAccount.text.toString()
            if (phone.isEmpty() || phone.isPhone().not()) {
                showErrorToast(R.string.plz_input_correct_phone)
                return@onClick
            }
            val authCode = binding.authCode.text.toString()
            if (loginType == 2) {
                if (authCode.isEmpty()) {
                    showErrorToast(R.string.plz_input_auth_code)
                    return@onClick
                }
            }
            val pwd = binding.password.text.toString()
            if (loginType == 1) {
                if (pwd.isEmpty() || pwd.length < 6) {
                    showErrorToast(R.string.plz_input_password)
                    return@onClick
                }
            }
            if (agree.not()) {
                showErrorToast(R.string.plz_agree_agreement_first)
                return@onClick
            }
            lifecycleScope.execute({
                val params = hashMapOf<String, Any?>()
                params["loginType"] = loginType
                if (loginType == 2) params["smsVerifyCode"] = authCode
                else if (loginType == 1) params["password"] = pwd
                params["phone"] = phone
                val user = postForData<User>(Api.Person.LOGIN, params)
                UserMMKV.user = user
                if (user.isInfoFinish) HomeActivity.start(this@LoginActivity)
                else UserInitActivity.start(this@LoginActivity)
                finish()
            }, null, { showProgressDialog() }, { dismissProgressDialog() })
        }
    }

    override fun doExtra() {
        if (AppConfigMMKV.agreeDialog.not()) {
            AgreeDialogFragment.show(
                supportFragmentManager,
                "",
                readTextFileFromAssets(this, "隐私政策.txt")
            )
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
        }
    }
}