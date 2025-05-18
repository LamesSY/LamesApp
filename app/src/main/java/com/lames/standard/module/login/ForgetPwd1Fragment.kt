package com.lames.standard.module.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import com.lames.standard.R
import com.lames.standard.common.CommonFragment
import com.lames.standard.common.Constants
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.databinding.FragmentForgetPwd1Binding
import com.lames.standard.network.Api
import com.lames.standard.network.launchX
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

class ForgetPwd1Fragment : CommonFragment<FragmentForgetPwd1Binding>() {

    private val phone by lazy { arguments?.getString(Constants.Params.ARG1) ?: EMPTY_STR }

    private var getAuthCodeJob: Job? = null

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgetPwd1Binding {
        return FragmentForgetPwd1Binding.inflate(inflater, container, false)
    }

    override fun initialization() {
        setAppBarTitle(R.string.set_pwd)
        if (phone.isNotEmpty()) {
            binding.editAccount.setText(phone)
            binding.editAccount.isEnabled = false
            binding.editAccount.alpha = 0.5f
        }
    }

    override fun bindEvent() {
        binding.getAuthCode.onClick {
            if (getAuthCodeJob.isRunning()) return@onClick
            val phone = binding.editAccount.text.toString()
            if (phone.isEmpty() || phone.isPhone().not()) {
                showErrorToast(R.string.plz_input_correct_phone)
                return@onClick
            }
            getAuthCodeJob = lifecycleScope.launchX {
                val result = postRequest(
                    Api.Person.SEND_AUTH_CODE,
                    hashMapOf<String, Any?>("phone" to binding.editAccount.text?.toString())
                )
                    .removeAllHeader("authorization")
                    .toAwaitResultBody<Any>()
                    .tryAwait()
                if (result?.code != 200) {
                    showErrorToast(result?.msg ?: "${result?.code}")
                    return@launchX
                }
                showSuccessToast(R.string.auth_code_send)
                repeat(60) {
                    binding.getAuthCode.text =
                        "${60 - it}${forString(R.string.get_again_after_second)}"
                    delay(1000)
                }
                binding.getAuthCode.setText(R.string.get_auth_code)
            }
        }

        binding.nextStep.onClick {
            val phone = binding.editAccount.text.toString()
            if (phone.isEmpty() || phone.isPhone().not()) {
                showErrorToast(R.string.plz_input_correct_phone)
                return@onClick
            }
            val code = binding.authCode.text.toString()
            if (code.isEmpty()) {
                showErrorToast(R.string.plz_input_auth_code)
                return@onClick
            }
            start<ForgetPwd2Fragment>(
                args = bundleOf(Constants.Params.ARG1 to phone, Constants.Params.ARG2 to code)
            )
        }
    }
}