package com.lames.standard.camera

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.android.BeepManager
import com.google.zxing.client.android.Intents
import com.hjq.permissions.Permission
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.network.launchX
import com.lames.standard.tools.forString
import com.lames.standard.tools.showErrorToast

abstract class AbsScanActivity<T : ViewBinding> : CommonActivity<T>(), BarcodeCallback {

    private var beepManager: BeepManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchX {
            val permissions = arrayListOf(Permission.CAMERA)
            val result =
                requestPermission(forString(R.string.desc_request_camera_permission), permissions)
            if (result) initScanView()
            else {
                showErrorToast(R.string.lack_of_needed_permission)
                finish()
            }
        }
    }

    abstract fun getBarCodeView(): DecoratedBarcodeView

    open fun onScanResult(result: BarcodeResult) {
        val intent = Intent()
        intent.putExtra(Constants.Params.ARG1, result.text)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun initScanView() {
        val codeTypeList =
            arrayListOf(BarcodeFormat.QR_CODE, BarcodeFormat.CODE_128, BarcodeFormat.CODE_39)
        getBarCodeView().decoderFactory =
            DefaultDecoderFactory(codeTypeList, null, null, Intents.Scan.MIXED_SCAN)
        getBarCodeView().decodeContinuous(this)
        getBarCodeView().setStatusText(EMPTY_STR)

        beepManager = BeepManager(this)
    }

    override fun barcodeResult(result: BarcodeResult?) {
        result ?: return
        beepManager?.playBeepSoundAndVibrate()
        getBarCodeView().pause()
        onScanResult(result)
    }

    override fun onResume() {
        super.onResume()
        getBarCodeView().resume()
    }

    override fun onPause() {
        super.onPause()
        getBarCodeView().pause()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return getBarCodeView().onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event)
    }

}