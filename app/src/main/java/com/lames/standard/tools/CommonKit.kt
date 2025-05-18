package com.lames.standard.tools

import android.content.Context
import android.media.AudioManager
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.lames.standard.common.CommonApp
import com.lames.standard.common.CommonDialogFragment
import kotlinx.coroutines.Job

/**
 * 获取字符串简便方法
 */
fun forString(resId: Int): String = CommonApp.obtain<CommonApp>().getString(resId)

/**
 * 获取颜色值简便方法
 */
fun forColor(resId: Int): Int = ContextCompat.getColor(CommonApp.obtain(), resId)

/**
 * 安全取消任务
 */
fun Job?.safeCancel() {
    this ?: return
    if ((this.isActive && this.isCancelled.not())) this.cancel()
}

/**
 * 任务是否正在执行
 */
fun Job?.isRunning(): Boolean {
    this ?: return false
    return this.isActive && this.isCancelled.not()
}

fun View.alwaysGetGesture() {
    this.setOnTouchListener { v, event ->
        if (v == this) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> v.parent.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> v.parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        v.performClick()
        return@setOnTouchListener false
    }
}

inline fun <reified T : CommonDialogFragment<*>> showDialogFragment(
    fragmentManager: FragmentManager,
    tag: String? = null,
    initDialog: ((T) -> Unit) = {}
) {
    tag?.let { if (fragmentManager.findFragmentByTag(it) != null) return }
    val d = T::class.java.getDeclaredConstructor().newInstance()
    initDialog.invoke(d)
    d.show(fragmentManager, tag)
}


fun getVolumePercentage(context: Context, streamType: Int): Float {
    val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    // 获取当前音量和最大音量
    val currentVolume = audioManager.getStreamVolume(streamType)
    val maxVolume = audioManager.getStreamMaxVolume(streamType)

    // 计算百分比
    return (currentVolume.toFloat() / maxVolume.toFloat())
}