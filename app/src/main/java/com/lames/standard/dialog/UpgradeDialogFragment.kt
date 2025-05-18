package com.lames.standard.dialog

import android.content.Intent
import android.net.Uri
import android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonApp
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.common.GlobalVar
import com.lames.standard.databinding.DialogFragmentUpgradeBinding
import com.lames.standard.mmkv.AppConfigMMKV
import com.lames.standard.network.launchX
import com.lames.standard.tools.canInstall
import com.lames.standard.tools.gone
import com.lames.standard.tools.installApk
import com.lames.standard.tools.invisible
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast
import com.lames.standard.tools.visible
import kotlinx.coroutines.flow.catch
import rxhttp.toDownloadFlow
import rxhttp.wrapper.param.RxHttp
import java.io.File

class UpgradeDialogFragment : CommonDialogFragment<DialogFragmentUpgradeBinding>() {

    private val permissionLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            if (CommonApp.obtain<App>().canInstall().not()) {
                showErrorToast(R.string.no_install_permission)
                binding.upgrade.visible()
                binding.downloadLayout.gone()
                //if (AppConfigMMKV.newVersion?.forceUpgrade != 1) dismiss(
                return@registerForActivityResult
            }
            onDownloadSuccess(apkPath)
        }

    private var apkPath = EMPTY_STR

    override fun getViewBinding(inflater: LayoutInflater): DialogFragmentUpgradeBinding {
        return DialogFragmentUpgradeBinding.inflate(inflater)
    }

    override fun onStart() {
        super.onStart()
        //isCancelable = AppConfigMMKV.newVersion?.forceUpgrade != 1
    }

    override fun initialization() {
        binding.versionCode.text = "${AppConfigMMKV.newVersion?.versionName}"
        binding.content.text = "${AppConfigMMKV.newVersion?.updateContent}"
        //binding.close.isVisible = AppConfigMMKV.newVersion?.forceUpgrade != 1
    }

    override fun bindEvent() {
        binding.close.onClick { dismiss() }

        binding.upgrade.onClick {
            binding.upgrade.invisible()
            binding.downloadLayout.visible()
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
        binding.downloadLayout.visible()
        binding.downloadProgress.progress = progress
        binding.downloadTitle.text = "已下载${progress}%"
    }

    private fun onDownloadError() {
        binding.downloadLayout.gone()
        binding.upgrade.visible()
        showErrorToast(R.string.download_failure)
    }

    private fun onDownloadSuccess(path: String) {
        apkPath = path
        binding.upgrade.invisible()
        binding.downloadLayout.visible()
        binding.downloadTitle.text = "安装中…"
        val app = CommonApp.obtain<App>()
        if (app.canInstall()) {
            val result = app.installApk(app, File(path), "${app.packageName}.fileProvider")
            //if (AppConfigMMKV.newVersion?.forceUpgrade != 1) dismiss()
            dismiss()
            if (result.not()) showErrorToast(R.string.install_failure)
            return
        }
        val intent = Intent(ACTION_MANAGE_UNKNOWN_APP_SOURCES)
        intent.data = Uri.parse("package:${app.packageName}")
        permissionLaunch.launch(intent)
    }

}