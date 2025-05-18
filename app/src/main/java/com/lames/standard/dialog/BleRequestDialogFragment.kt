package com.lames.standard.dialog

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.view.LayoutInflater
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentManager
import com.hjq.permissions.XXPermissions
import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonApp
import com.lames.standard.common.CommonDialogFragment
import com.lames.standard.databinding.DialogFragmentBleRequestBinding
import com.lames.standard.tools.forString
import com.lames.standard.tools.getBlePermissions
import com.lames.standard.tools.goToGpsSetting
import com.lames.standard.tools.isGpsOpen
import com.lames.standard.tools.onClick
import com.lames.standard.tools.showErrorToast

/**
 * 专门用于申请蓝牙权限、判断有无蓝牙功能，开启蓝牙开关的dialog
 */
class BleRequestDialogFragment : CommonDialogFragment<DialogFragmentBleRequestBinding>() {

    private var onRequestFailure: (() -> Unit)? = null
    private var onRequestSuccess: (() -> Unit)? = null

    private var hasPermission: Boolean = false
    private var bleIsEnable: Boolean = false

    private val bleManager by lazy { requireContext().getSystemService(BluetoothManager::class.java) }

    private val bleLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != Activity.RESULT_OK) {
                onRequestFailure?.invoke()
                showErrorToast(R.string.desc_ble_not_open)
                dismiss()
                return@registerForActivityResult
            }
            if (isGpsOpen(requireContext())) {
                onRequestSuccess?.invoke()
                dismiss()
            } else {
                hasPermission = true
                bleIsEnable = true
                doExtra()
            }
        }

    override fun getViewBinding(inflater: LayoutInflater): DialogFragmentBleRequestBinding {
        return DialogFragmentBleRequestBinding.inflate(inflater)
    }

    override fun initialization() {
        hasPermission = XXPermissions.isGranted(requireContext(), getBlePermissions())
        binding.bottomLayout.isVisible = hasPermission.not()
        if (hasPermission.not()) return

        val bleManager = requireContext().getSystemService(BluetoothManager::class.java)
        bleIsEnable = bleManager.adapter.isEnabled
        if (bleIsEnable.not()) {
            binding.alertContent.text = forString(R.string.desc_request_ble_for_device)
            bleLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    override fun bindEvent() {
        binding.cancel.onClick {
            onRequestFailure?.invoke()
            dismiss()
        }

        binding.confirm.onClick {
            XXPermissions.with(requireContext()).permission(getBlePermissions())
                .request { _, allGranted ->
                    if (allGranted.not()) {
                        showErrorToast(R.string.lack_of_needed_permission)
                        onRequestFailure?.invoke()
                        dismiss()
                        return@request
                    }
                    if (bleManager.adapter == null) {
                        showErrorToast(R.string.no_ble_function)
                        onRequestFailure?.invoke()
                        dismiss()
                        return@request
                    }
                    if (bleManager.adapter.isEnabled.not()) {
                        bleLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
                        return@request
                    }
                    onRequestSuccess?.invoke()
                    dismiss()
                }
        }
    }

    override fun doExtra() {
        if (hasPermission.not() || bleIsEnable.not()) return
        if (isGpsOpen(requireContext())) return
        binding.bottomLayout.isVisible = true
        binding.alertContent.text = forString(R.string.request_location_for_device)
        binding.confirm.onClick {
            requireActivity().goToGpsSetting()
            dismiss()
        }
    }

    companion object {
        /**
         * 包含蓝牙权限、位置开关、蓝牙开关检查。如果都已获取及开启则会不显示弹窗直接执行 onRequestSuccess
         */
        fun show(
            fm: FragmentManager,
            onRequestFailure: (() -> Unit)? = null,
            onRequestSuccess: (() -> Unit)? = null
        ) {
            //先检查有没有蓝牙功能，没有就直接关闭
            val bleManager = CommonApp.obtain<App>().getSystemService(BluetoothManager::class.java)
            if (bleManager.adapter == null) {
                showErrorToast(R.string.no_ble_function)
                return
            }
            //如果已经赋予了权限并且也打开了蓝牙功能，则直接执行后续逻辑
            val hasPermission = XXPermissions.isGranted(CommonApp.obtain(), getBlePermissions())
            if (hasPermission && bleManager.adapter.isEnabled && isGpsOpen(CommonApp.obtain())) {
                onRequestSuccess?.invoke()
                return
            }
            //否则开始申请相关权限
            BleRequestDialogFragment().also {
                it.onRequestFailure = onRequestFailure
                it.onRequestSuccess = onRequestSuccess
            }.show(fm, null)
        }

    }
}