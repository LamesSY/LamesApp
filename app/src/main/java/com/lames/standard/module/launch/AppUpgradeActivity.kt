package com.lames.standard.module.launch

import android.content.Intent
import android.net.Uri
import android.provider.Settings.*
import android.view.LayoutInflater
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.CommonApp
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.common.GlobalVar
import com.lames.standard.databinding.ActivityAppUpgradeBinding
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.module.home.HomeActivity
import com.lames.standard.network.launchX
import com.lames.standard.tools.canInstall
import com.lames.standard.tools.installApk
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast
import kotlinx.coroutines.flow.catch
import rxhttp.toDownloadFlow
import rxhttp.wrapper.param.RxHttp
import java.io.File

class AppUpgradeActivity : CommonActivity<ActivityAppUpgradeBinding>() {

    private val permissionLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            if (CommonApp.obtain<App>().canInstall().not()) {
                showErrorToast(R.string.no_install_permission)
                binding.upgrade.isVisible = true
                binding.downloadLayout.isInvisible = true
                /*if (AppConfigMMKV.newVersion?.forceUpgrade != 1) {
                    HomeActivity.start(this)
                    finish()
                }*/
                return@registerForActivityResult
            }
            onDownloadSuccess(apkPath)
        }

    private var apkPath = EMPTY_STR

    override fun getViewBinding() = ActivityAppUpgradeBinding.inflate(LayoutInflater.from(this))

    override fun onStart() {
        super.onStart()
        val isForceUpgrade = false//AppConfigMMKV.newVersion?.forceUpgrade == 1
        binding.close.isVisible = isForceUpgrade.not()
        onBackPressedDispatcher.addCallback(enabled = isForceUpgrade) {}
    }

    override fun initialization() {
        binding.versionCode.text = "${AppConfigMMKV.newVersion?.versionName}"
        binding.content.text = "${AppConfigMMKV.newVersion?.updateContent}"
        //binding.close.isVisible = AppConfigMMKV.newVersion?.forceUpgrade != 1


        binding.downloadProgress.trackThickness = binding.upgrade.height
    }

    override fun bindEvent() {
        binding.close.onClick {
            HomeActivity.start(this)
            finish()
        }

        binding.upgrade.onClick {
            binding.upgrade.isInvisible = true
            binding.downloadLayout.isVisible = true
            lifecycleScope.launchX {
                val apkName = "shenAI.apk"
                val path = GlobalVar.obtain().downloadPath + File.separator + apkName
                RxHttp.get(AppConfigMMKV.newVersion?.downloadUrl).toDownloadFlow(path)
                    .onProgress { onDownloading(it.progress) }
                    .catch { onDownloadError() }
                    .collect { onDownloadSuccess(it) }
            }
        }
    }

    private fun onDownloading(progress: Int) {
        binding.downloadLayout.isVisible = true
        binding.downloadProgress.progress = progress
        binding.downloadTitle.text = "已下载${progress}%"
    }

    private fun onDownloadError() {
        binding.downloadLayout.isInvisible = true
        binding.upgrade.isVisible = true
        showErrorToast(R.string.download_failure)
    }

    private fun onDownloadSuccess(path: String) {
        apkPath = path
        binding.upgrade.isInvisible = true
        binding.downloadLayout.isVisible = true
        binding.downloadTitle.text = "安装中…"
        val app = CommonApp.obtain<App>()
        if (app.canInstall()) {
            val result = app.installApk(app, File(path), "${app.packageName}.fileProvider")
            /*if (AppConfigMMKV.newVersion?.forceUpgrade != 1) {

            }*/
            if (result.not()) showErrorToast(R.string.install_failure)
            return
        }
        val intent = Intent(ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:${app.packageName}")
        permissionLaunch.launch(intent)
    }

}