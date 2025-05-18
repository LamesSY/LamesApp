package com.lames.standard.webx

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.webkit.JavascriptInterface
import androidx.annotation.RequiresApi
import androidx.fragment.app.findFragment
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.CommonApp
import com.lames.standard.common.CommonFragment
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.dialog.AlertDialogFragment
import com.lames.standard.entity.NativeNavigateParams
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.tools.AppKit
import com.lames.standard.tools.LogKit
import com.lames.standard.tools.forString
import com.lames.standard.tools.getVolumePercentage
import com.lames.standard.tools.parseToJson
import com.lames.standard.tools.showErrorToast
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

open class WebViewFragmentJsBridge(private val webView: BaseWebView) {

    private val fg: CommonFragment<*>
        get() {
            val findFragment = runCatching { webView.findFragment<CommonFragment<*>>() }.getOrNull()
            if (findFragment != null) return findFragment
            val topAty = AppKit.obtain().topActivity() as CommonActivity<*>
            return topAty.supportFragmentManager.fragments[0] as CommonFragment<*>
        }

    @JavascriptInterface
    fun getTokenInfo(): String {
        val user = UserMMKV.user
        val params = hashMapOf(
            "token" to (user?.token ?: EMPTY_STR),
        )
        return parseToJson(params)
    }

    @JavascriptInterface
    fun setTitle(title: String) {
        fg.requireActivity().runOnUiThread { fg.setAppBarTitle(title) }
    }

    @JavascriptInterface
    open fun openPageContainer(url: String, title: String?, jsonParams: String?) {
        LogKit.d(javaClass.simpleName, "$jsonParams")
        val jsonObject = runCatching { JSONObject(jsonParams!!) }.getOrNull()
        val titleBarStyle = jsonObject?.optInt("titleBarStyle") ?: 0
        WebViewXActivity.start(fg.requireActivity(), url, title, titleBarStyle)
    }

    @JavascriptInterface
    open fun openPage(url: String, title: String?, jsonParams: String?) {
        LogKit.d(javaClass.simpleName, "$jsonParams")

    }

    @JavascriptInterface
    fun openNativePage(nativePageType: Int, jsonParams: String?) {
        val nativeNavigateParams = try {
            val jsonObject = JSONObject(jsonParams!!)
            NativeNavigateParams(
                jsonObject.optLong("id"),
                jsonObject.optString("uId"),
                jsonObject.optLong("time"),
                jsonObject.optString("duid"),
                jsonObject.optString("deviceModelDuid"),
                jsonObject.optString("deviceUuid"),
            )
            //用下面这种方式来解析 json 在混淆时会出问题，原因暂时未找到
            //parseToObject<NativeNavigateParams>(jsonParams!!)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
        LogKit.d(javaClass.simpleName, "$nativePageType-$jsonParams")
    }

    @JavascriptInterface
    fun pop(closeType: Int) {
        LogKit.d(javaClass.simpleName, "pop(${closeType})")
        fg.requireActivity().runOnUiThread {
            if (closeType == 3) fg.requireActivity().finish()
            else fg.dispatcher.onBackPressed()
        }
    }

    @JavascriptInterface
    fun savePictureToGallery(picName: String, base64: String, endMethod: String) {
        runCatching {
            val decodeString = base64.split(",").lastOrNull()
            val decode = Base64.decode(decodeString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmapQ(picName, bitmap)
            } else requestSaveBitmap(picName, bitmap)
            fg.requireActivity().runOnUiThread { webView.loadUrl("javascript:${endMethod}") }
        }
    }

    @JavascriptInterface
    fun savePhotoToAlbum(base64: String) {
        runCatching {
            val decodeString = base64.split(",").lastOrNull()
            val decode = Base64.decode(decodeString, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size)
            val picName = "ts_pic_${UserMMKV.user?.uid}"
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveBitmapQ(picName, bitmap)
            } else requestSaveBitmap(picName, bitmap)
        }
    }

    private fun requestSaveBitmap(picName: String, bitmap: Bitmap) {
        val hasPermission =
            XXPermissions.isGranted(fg.requireContext(), Permission.WRITE_EXTERNAL_STORAGE)
        if (hasPermission) saveBitmap(picName, bitmap)
        else fg.showDialogFg<AlertDialogFragment> {
            it.title = forString(R.string.alert_tip_title)
            it.content = "为了将图片保存到图库中，将向您申请存储读写权限"
            it.onConfirm = {
                XXPermissions.with(fg.requireContext()).unchecked()
                    .permission(Permission.WRITE_EXTERNAL_STORAGE).request { _, allGranted ->
                    if (allGranted.not()) showErrorToast(R.string.lack_of_needed_permission)
                    else saveBitmap(picName, bitmap)
                }
            }
        }
    }

    private fun saveBitmap(picName: String, bitmap: Bitmap) {
        val picturePath =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath
        val path = picturePath + File.separator + forString(R.string.app_name)
        File(path).mkdir()
        val fileName = picName + System.currentTimeMillis() + ".png"
        val file = File(path, fileName)
        try {
            val outputStream = FileOutputStream(file)
            outputStream.use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            val uri = Uri.fromFile(file)
            CommonApp.obtain<App>()
                .sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveBitmapQ(fileName: String, bitmap: Bitmap) {
        val contentValues = ContentValues().apply {
            put(
                MediaStore.Images.Media.DISPLAY_NAME,
                System.currentTimeMillis().toString() + fileName + ".png"
            )
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(
                MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + File.separator + forString(R.string.app_name)
            )
        }
        val saveUri = fg.requireContext().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        if (saveUri == null) {
            showErrorToast(R.string.save_failure)
            return
        }
        val outputStream: OutputStream? =
            fg.requireContext().contentResolver.openOutputStream(saveUri)
        if (outputStream == null) {
            showErrorToast(R.string.save_failure)
            return
        }
        val result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        if (result.not()) showErrorToast(R.string.save_failure)
    }

    @JavascriptInterface
    fun getCurrentMediaVolume(): Float {
        return getVolumePercentage(CommonApp.obtain<App>(), AudioManager.STREAM_MUSIC)
    }

}