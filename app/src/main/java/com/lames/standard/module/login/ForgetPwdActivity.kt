package com.lames.standard.module.login

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityContainerBinding
import com.lames.standard.tools.forColor

class ForgetPwdActivity : CommonActivity<ActivityContainerBinding>() {

    private val phone by lazy { intent.getStringExtra(Constants.Params.ARG1) }

    override fun getViewBinding() = ActivityContainerBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        window.statusBarColor = forColor(R.color.md_theme_primary)
        supportFragmentManager.commit {
            add(
                binding.fcView.id,
                ForgetPwd1Fragment::class.java,
                bundleOf(Constants.Params.ARG1 to phone)
            )
        }
    }

    companion object {
        fun start(context: Context, phone: String? = null) {
            val intent = Intent(context, ForgetPwdActivity::class.java)
            intent.putExtra(Constants.Params.ARG1, phone)
            context.startActivity(intent)
        }
    }
}