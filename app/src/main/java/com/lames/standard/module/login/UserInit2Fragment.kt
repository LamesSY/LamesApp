package com.lames.standard.module.login

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.buildSpannedString
import androidx.core.text.scale
import androidx.lifecycle.lifecycleScope
import com.lames.standard.R
import com.lames.standard.common.CommonFragment
import com.lames.standard.databinding.FragmentUserInit2Binding
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.module.home.HomeActivity
import com.lames.standard.network.execute
import com.lames.standard.oss.OssKit
import com.lames.standard.tools.onClick

class UserInit2Fragment : CommonFragment<FragmentUserInit2Binding>() {

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentUserInit2Binding {
        return FragmentUserInit2Binding.inflate(inflater, container, false)
    }

    override fun bindEvent() {
        binding.heightRuler.setOnValueChangedListener {
            binding.height.text = buildSpannedString {
                append("${it.toInt()}")
                scale(0.6f) { append("厘米") }
            }
        }

        binding.weightRuler.setOnValueChangedListener {
            binding.weight.text = buildSpannedString {
                append("${it.toInt()}")
                scale(0.6f) { append("公斤") }
            }
        }

        binding.commit.onClick {
            (requireActivity() as UserInitActivity).tempUser.apply {
                height = binding.heightRuler.currentValue.toDouble()
                weight = binding.weightRuler.currentValue.toDouble()
            }
            lifecycleScope.execute({
                val tempUser = (requireActivity() as UserInitActivity).tempUser

                tempUser.avatar = (requireActivity() as UserInitActivity).avatarLocalPath?.let {
                    if (it.isEmpty()) return@let null
                    OssKit.uploadFile(OssKit.userAvatarFolder, OssKit.userAvatarName, it) {}
                }

                val params1 = hashMapOf<String, Any?>()
                params1["nickName"] = tempUser.nickName
                params1["familyUserId"] = UserMMKV.user?.uid
                params1["mainUserId"] = UserMMKV.user?.uid
                params1["avatar"] = tempUser.avatar
                //postForResult<Any>(Api.Relative.UPDATE_RELATIVE_INFO, params1)

                val params = hashMapOf<String, Any?>()
                params["height"] = tempUser.height
                params["weight"] = tempUser.weight
                params["m62"] = if (tempUser.m62 > 0) tempUser.m62 else 60.0
                params["sex"] = tempUser.sex
                params["birthday"] = tempUser.birthday
                params["uid"] = UserMMKV.user?.uid
                //postForResult<Any>(Api.Person.UPDATE_PERSONAL_INFO, params)

                UserMMKV.user = tempUser
                HomeActivity.start(requireContext())
                requireActivity().finish()
            }, null, { showProgressDialog(R.string.operating) }, { dismissProgressDialog() })

        }
    }

    override fun doExtra() {
        binding.heightRuler.setValue(120f, 190f, 160f, 1f, 10)
        binding.weightRuler.setValue(30f, 120f, 60f, 1f, 10)
    }
}