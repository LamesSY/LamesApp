package com.lames.standard.module.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonApp
import com.lames.standard.common.CommonFragment
import com.lames.standard.common.Constants
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.databinding.FragmentForgetPwd2Binding
import com.lames.standard.network.Api
import com.lames.standard.network.execute
import com.lames.standard.network.postRequest
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast
import com.lames.standard.tools.showSuccessToast
import rxhttp.wrapper.param.toAwaitResultBody

class ForgetPwd2Fragment : CommonFragment<FragmentForgetPwd2Binding>() {

    private val phone by lazy { arguments?.getString(Constants.Params.ARG1) ?: EMPTY_STR }
    private val code by lazy { arguments?.getString(Constants.Params.ARG2) ?: EMPTY_STR }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentForgetPwd2Binding {
        return FragmentForgetPwd2Binding.inflate(inflater, container, false)
    }

    override fun initialization() {
        setAppBarTitle(R.string.set_pwd)
    }

    override fun bindEvent() {
        binding.finish.onClick {
            val pwd1 = binding.pwd1.text.toString()
            val pwd2 = binding.pwd2.text.toString()
            if (pwd1.isEmpty() || pwd2.isEmpty()) {
                showErrorToast(R.string.plz_input_password)
                return@onClick
            }
            if (pwd1 != pwd2) {
                showErrorToast(R.string.confirm_password_not_same)
                return@onClick
            }
            lifecycleScope.execute({
                val params = hashMapOf<String, Any?>()
                params["phone"] = phone
                params["password"] = pwd1
                params["verifyCode"] = code
                postRequest(Api.Person.RESET_PASSWORD, params).removeAllHeader("authorization")
                    .toAwaitResultBody<Any>().await()
                showSuccessToast(R.string.set_pwd_success_then_login)
                CommonApp.obtain<App>().onSignOut()
            }, null, { showProgressDialog(R.string.operating) }, { dismissProgressDialog() })
        }

    }
}