package com.lames.standard.module.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.databinding.ActivityHomeBinding
import com.lames.standard.tools.ToastKit
import com.lames.standard.tools.forString

class HomeActivity : CommonActivity<ActivityHomeBinding>(), TabLayout.OnTabSelectedListener {

    private val fragmentList by lazy { mutableListOf(HomeFragment(), MineFragment()) }

    private var callbackTime = 0L
    private lateinit var callback: OnBackPressedCallback

    override fun getViewBinding(): ActivityHomeBinding =
        ActivityHomeBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        callback = onBackPressedDispatcher.addCallback {
            val now = System.currentTimeMillis()
            val diff = now - callbackTime
            callbackTime = now
            if (diff >= 2000) {
                ToastKit.show("${forString(R.string.quit_if_again)}${forString(R.string.app_main_name)}")
            } else {
                callback.isEnabled = false
                onBackPressedDispatcher.onBackPressed()
            }
        }
        binding.homeVp.isUserInputEnabled = false
        binding.homeVp.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragmentList.size

            override fun createFragment(position: Int) = fragmentList[position]

        }

        TabLayoutMediator(binding.homeTabLayout, binding.homeVp, true, false) { tab, position ->
            tab.setText(if (position == 0) R.string.home_page else R.string.mine)
        }.attach()
    }


    override fun onTabSelected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, HomeActivity::class.java)
            context.startActivity(intent)
        }
    }
}