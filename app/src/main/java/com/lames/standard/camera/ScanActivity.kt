package com.lames.standard.camera

import android.content.Intent
import android.view.LayoutInflater
import com.google.zxing.client.android.BeepManager
import com.huawei.hms.ml.scan.HmsScan
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityScanBinding
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.onClick

class ScanActivity : AbsScanKitActivity<ActivityScanBinding>() {

    private val beepManager by lazy { BeepManager(this) }

    override fun getViewBinding() = ActivityScanBinding.inflate(LayoutInflater.from(this))

    override fun getRemoteViewParent() = binding.barcodeView

    override fun bindEvent() {
        binding.close.onClick { onBackPressedDispatcher.onBackPressed() }
    }

    override fun onScanResult(results: Array<HmsScan>) {
        val content = results.getOrNull(0)?.getOriginalValue() ?: return
        beepManager.playBeepSoundAndVibrate()
        remoteView?.pauseContinuouslyScan()
        LogKit.d(javaClass.simpleName, content)
        val intent = Intent()
        intent.putExtra(Constants.Params.ARG1, content)
        setResult(RESULT_OK, intent)
        finish()
    }

}