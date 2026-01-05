package com.lames.standard.module.launch

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.webkit.WebStorage
import androidx.lifecycle.lifecycleScope
import com.lames.standard.App
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.CommonApp
import com.lames.standard.databinding.ActivitySplashBinding
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.network.launchX
import com.lames.standard.tools.versionCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import rxhttp.RxHttpPlugins

@SuppressLint("CustomSplashScreen")
class SplashActivity : CommonActivity<ActivitySplashBinding>() {

    override fun getViewBinding() = ActivitySplashBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        lifecycleScope.launchX {
            AppConfigMMKV.checkNewVersion()
            AppConfigMMKV.getPreviewVersion()
            val version = CommonApp.obtain<App>().versionCode()
            if (version > AppConfigMMKV.adaptAppVersion) {
                withContext(Dispatchers.IO) {
                    WebStorage.getInstance().deleteAllData()
                    RxHttpPlugins.getCache().removeAll()
                }
            }
            AppConfigMMKV.adaptAppVersion = version

            //finish()
        }
    }

    override fun bindEvent() {

    }
}