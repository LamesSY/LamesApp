package com.lames.standard.webx

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient.*
import android.webkit.WebView
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.network.launchX
import com.lames.standard.tools.forString
import com.lames.standard.tools.showErrorToast

abstract class AbsWebViewActivity<T : ViewBinding> : CommonActivity<T>(), WebHost {

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val multiplePickMedia =
        registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) { uriList ->
            filePathCallback?.onReceiveValue(uriList.toTypedArray())
            filePathCallback = null
        }

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            val list = uri?.let { arrayOf(it) } ?: arrayOf()
            filePathCallback?.onReceiveValue(list)
            filePathCallback = null
        }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            val list = uri?.let { arrayOf(it) } ?: arrayOf()
            filePathCallback?.onReceiveValue(list)
            filePathCallback = null
        }

    private val multiFilePickerLauncher =
        registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uriList ->
            filePathCallback?.onReceiveValue(uriList.toTypedArray())
            filePathCallback = null
        }

    fun onAbsShowFileChooser(
        webView: WebView?,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?,
    ): Boolean {
        this.filePathCallback = filePathCallback
        val acceptTypes = fileChooserParams?.acceptTypes?.filter { it.isNotEmpty() }
        val mimeType = parseAcceptTypes(acceptTypes)
        val isMultiple = fileChooserParams?.mode == FileChooserParams.MODE_OPEN_MULTIPLE
        if (mimeType.contains("image")) {
            if (isMultiple) multiplePickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            else pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            return true
        }

        val hasPermission = XXPermissions.isGranted(this, Permission.MANAGE_EXTERNAL_STORAGE)
        if (hasPermission) {
            this.filePathCallback = null
            lifecycleScope.launchX {
                val result = requestPermission(
                    forString(R.string.desc_request_storage_permission),
                    listOf(Permission.MANAGE_EXTERNAL_STORAGE),
                )
                showErrorToast(if (result) R.string.permissions_granted else R.string.lack_of_needed_permission)
            }
            return false
        }

        if (isMultiple) multiFilePickerLauncher.launch(arrayOf(mimeType))
        else filePickerLauncher.launch(mimeType)
        return true
    }

    private fun parseAcceptTypes(acceptTypes: List<String>?): String {
        return when {
            acceptTypes.isNullOrEmpty() -> "*/*" // 默认所有文件
            acceptTypes.any { it.contains("image/*") } -> "image/*"
            acceptTypes.any { it.contains("application/pdf") } -> "application/pdf"
            else -> acceptTypes.first() // 取第一个有效类型
        }
    }

    override fun getHostContext() = this
    override fun getHostLifecycleScop() = lifecycleScope
    override fun setBarTitle(title: String) {
        setAppBarTitle(title)
    }

    override fun finishPage() {
        finish()
    }

    override fun getHostActivity() = this
}