package com.lames.standard.tools

import android.app.Activity
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.Color
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lames.standard.R
import java.io.ByteArrayOutputStream


fun showErrorToast(content: String) {
    ToastKit.makeCustomized(R.layout.toast_middle, {
        it.findViewById<ImageView>(R.id.toastIcon).setImageResource(R.drawable.ic_error)
        it.findViewById<TextView>(R.id.toastMsg).text = content
    }, gravity = Gravity.CENTER)
}

fun showErrorToast(stringRes: Int) {
    ToastKit.makeCustomized(R.layout.toast_middle, {
        it.findViewById<ImageView>(R.id.toastIcon).setImageResource(R.drawable.ic_error)
        it.findViewById<TextView>(R.id.toastMsg).setText(stringRes)
    }, gravity = Gravity.CENTER)
}

fun showSuccessToast(content: String) {
    ToastKit.makeCustomized(R.layout.toast_middle, {
        it.findViewById<ImageView>(R.id.toastIcon).setImageResource(R.drawable.ic_done)
        it.findViewById<TextView>(R.id.toastMsg).text = content
    }, gravity = Gravity.CENTER)
}

fun showSuccessToast(stringRes: Int) {
    ToastKit.makeCustomized(R.layout.toast_middle, {
        it.findViewById<ImageView>(R.id.toastIcon).setImageResource(R.drawable.ic_done)
        it.findViewById<TextView>(R.id.toastMsg).setText(stringRes)
    }, gravity = Gravity.CENTER)
}

fun showIconToast(content: String, iconRes: Int) {
    ToastKit.makeCustomized(R.layout.toast_middle, {
        it.findViewById<TextView>(R.id.toastMsg).text = content
        it.findViewById<ImageView>(R.id.toastIcon).setImageResource(iconRes)
    }, gravity = Gravity.CENTER)
}

fun showIconToast(stringRes: Int, iconRes: Int) {
    ToastKit.makeCustomized(R.layout.toast_middle, {
        it.findViewById<TextView>(R.id.toastMsg).setText(stringRes)
        it.findViewById<ImageView>(R.id.toastIcon).setImageResource(iconRes)
    }, gravity = Gravity.CENTER)
}

fun RecyclerView.addPaddingItemDecoration() {
    val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
    decoration.setDrawable(
        ContextCompat.getDrawable(
            context,
            R.drawable.shape_layout_divider_with_padding
        ) ?: return
    )
    addItemDecoration(decoration)
}

fun RecyclerView.addItemDecoration() {
    val decoration = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
    decoration.setDrawable(
        ContextCompat.getDrawable(context, R.drawable.shape_layout_divider) ?: return
    )
    addItemDecoration(decoration)
}

fun bmpToByteArray(bmp: Bitmap, needRecycle: Boolean): ByteArray? {
    val output = ByteArrayOutputStream()
    bmp.compress(CompressFormat.PNG, 100, output)
    if (needRecycle) {
        bmp.recycle()
    }
    val result = output.toByteArray()
    try {
        output.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return result
}

fun createScaleFontSpan(content: String, scale: Float = 1.3f): CharSequence {
    if (content.isEmpty()) return content
    return SpannableString(content).apply {
        setSpan(
            RelativeSizeSpan(scale),
            0,
            content.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

/**
 * 透明状态栏
 */
@Suppress("DEPRECATION")
fun Activity.transparentStatusBar(useDarkStatusBarText: Boolean) {
    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    window.statusBarColor = Color.TRANSPARENT
    //如果是黑夜模式则isLightStatusBar要取反
    var isDarkStatusBarText = useDarkStatusBarText
    if ((resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
        isDarkStatusBarText = !isDarkStatusBarText
    }
    window.decorView.apply {
        systemUiVisibility =
            (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            systemUiVisibility =
                if (isDarkStatusBarText) systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}