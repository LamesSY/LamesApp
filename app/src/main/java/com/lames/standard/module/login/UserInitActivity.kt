package com.lames.standard.module.login

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.fragment.app.commit
import com.lames.standard.common.CommonActivity
import com.lames.standard.databinding.ActivityContainerBinding
import com.lames.standard.entity.User
import com.lames.standard.mmkv.UserMMKV

class UserInitActivity : CommonActivity<ActivityContainerBinding>() {

    lateinit var tempUser: User
    var avatarLocalPath: String? = null

    override fun getViewBinding() = ActivityContainerBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        tempUser = UserMMKV.user!!
        supportFragmentManager.commit {
            add(binding.fcView.id, UserInit1Fragment::class.java, null)
        }
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, UserInitActivity::class.java)
            context.startActivity(intent)
        }
    }
}