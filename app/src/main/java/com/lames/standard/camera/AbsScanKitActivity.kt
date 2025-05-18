package com.lames.standard.camera

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.hjq.permissions.Permission
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.ml.scan.HmsScan
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.GlobalVar
import com.lames.standard.network.launchX
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.dp2px
import com.lames.standard.tools.forString
import com.lames.standard.tools.showErrorToast

/**
 * 基于华为ScanKit的二维码扫码功能，识别灵敏度更高
 */
abstract class AbsScanKitActivity<T : ViewBinding> : CommonActivity<T>() {

    protected var remoteView: RemoteView? = null

    abstract fun onScanResult(results: Array<HmsScan>)
    abstract fun getRemoteViewParent(): ViewGroup

    override fun onStart() {
        super.onStart()
        remoteView?.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launchX {
            val r = requestPermission(
                forString(R.string.desc_request_camera_permission),
                mutableListOf(Permission.CAMERA)
            )
            if (r.not()) {
                showErrorToast(R.string.lack_of_needed_permission)
                finish()
            }
            remoteView = buildRemoteView()
            remoteView?.onCreate(savedInstanceState)
            val params = FrameLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
            getRemoteViewParent().addView(remoteView, params)
            remoteView?.setOnResultCallback { result ->
                val s = result?.getOrNull(0)?.getOriginalValue() ?: "empty"
                LogKit.d(javaClass.simpleName, s)
                onScanResult(result)
            }
        }
    }

    private fun buildRemoteView(): RemoteView {
        val scanRect = calScanRect()
        return RemoteView.Builder().setContext(this)
            .setFormat(
                HmsScan.QRCODE_SCAN_TYPE,
                HmsScan.CODE128_SCAN_TYPE,
                HmsScan.DATAMATRIX_SCAN_TYPE
            )
            .setContinuouslyScan(false)
            .setBoundingBox(scanRect)
            .build()
    }

    private fun calScanRect(): Rect {
        val sw = GlobalVar.obtain().screenWith
        val sh = GlobalVar.obtain().screenHeight
        val scanFrameSize = dp2px(300)
        val rect = Rect()
        rect.left = (sw / 2 - scanFrameSize / 2)
        rect.right = (sw / 2 + scanFrameSize / 2)
        rect.top = (sh / 2 - scanFrameSize / 2)
        rect.bottom = (sh / 2 + scanFrameSize / 2)
        return rect
    }


    override fun onResume() {
        super.onResume()
        remoteView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView?.onDestroy()
    }

    override fun onStop() {
        super.onStop()
        remoteView?.onStop()
    }

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, AbsScanKitActivity::class.java)
            context.startActivity(intent)
        }
    }
}