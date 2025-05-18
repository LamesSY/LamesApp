@file:JvmName("UIKit")
@file:Suppress("DEPRECATION")

package com.lames.standard.tools

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.text.Editable
import android.text.TextWatcher
import android.util.TypedValue
import android.view.Gravity
import android.view.Menu
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.doOnPreDraw
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.lames.standard.App
import com.lames.standard.common.CommonApp
import kotlin.math.abs


/**
 * Created by Jason
 */

const val HIDE_UI_FLAG =
    (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)

private const val SHOW_UI_FLAG =
    View.SYSTEM_UI_FLAG_VISIBLE or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

private var sDecorViewDelta = 0

val textWatcherTagId by lazy { View.generateViewId() }

val Float.dp
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        Resources.getSystem().displayMetrics
    )

fun Context.dp2px(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()

fun View.dp2px(dp: Int): Int = (dp * resources.displayMetrics.density + 0.5f).toInt()

fun Context.getScreenWidth(): Int = resources.displayMetrics.widthPixels

fun Context.getScreenHeight(): Int = resources.displayMetrics.heightPixels

fun View.getDimension(block: (width: Int, height: Int) -> Unit) {
    val w = measuredWidth
    val h = measuredHeight
    if (w == 0 && h == 0) doOnPreDraw { block(it.measuredWidth, it.measuredHeight) }
    else block(w, h)
}

inline fun View.onClick(crossinline function: () -> Unit) = setOnClickListener { function() }

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

inline fun View.singleTap(interval: Long = 500L, crossinline function: () -> Unit) {
    var lastClick = 0L
    setOnClickListener {
        val gap = System.currentTimeMillis() - lastClick
        lastClick = System.currentTimeMillis()
        if (gap < interval) return@setOnClickListener
        function.invoke()
    }
}

fun Activity.hideSystemUI() =
    runOnUiThread { window?.decorView?.let { it.systemUiVisibility = HIDE_UI_FLAG } }

fun Activity.showSystemUI() =
    runOnUiThread { window?.decorView?.let { it.systemUiVisibility = SHOW_UI_FLAG } }

fun DialogFragment.hideSystemUI() =
    dialog?.window?.decorView?.let { it.systemUiVisibility = HIDE_UI_FLAG }

fun DialogFragment.showSystemUI() =
    dialog?.window?.decorView?.let { it.systemUiVisibility = SHOW_UI_FLAG }

fun Dialog.hideSystemUI() = window?.decorView?.let { it.systemUiVisibility = HIDE_UI_FLAG }
fun Dialog.showSystemUI() = window?.decorView?.let { it.systemUiVisibility = SHOW_UI_FLAG }

fun Context.showKeyboard() {
    val imm = getSystemService<InputMethodManager>() ?: return
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard() {
    window.currentFocus?.let {
        hideKeyboard(it)
        return
    }

    val decorView = window.decorView
    val kfv = decorView.findViewWithTag<View>("keyboardFocusView")
    kfv?.let {
        hideKeyboard(it)
        return
    }

    val editText = EditText(window.context)
    decorView.tag = "keyboardFocusView"
    (decorView as ViewGroup).addView(editText, 0, 0)
}

fun Activity.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    view.isFocusable = true
    view.isFocusableInTouchMode = true
    view.requestFocus()
    imm.showSoftInput(view, 0)
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Activity.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Activity.isKeyboardVisible(): Boolean {
    val decorView = window.decorView
    val outRect = Rect()
    decorView.getWindowVisibleDisplayFrame(outRect)
    val delta: Int = abs(decorView.bottom - outRect.bottom)
    if (delta <= getNavBarHeight() + getStatusBarHeight()) {
        sDecorViewDelta = delta
        return false
    }
    return delta - sDecorViewDelta > 0
}

fun isViewBeingTouched(v: View?, event: MotionEvent): Boolean {
    if (v is EditText) {
        val l = intArrayOf(0, 0)
        v.getLocationOnScreen(l)
        val left = l[0]
        val top = l[1]
        val bottom: Int = top + v.getHeight()
        val right: Int = left + v.getWidth()
        return !(event.rawX > left && event.rawX < right && event.rawY > top && event.rawY < bottom)
    }
    return false
}

fun getStatusBarHeight(): Int {
    val resources = Resources.getSystem()
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    return resources.getDimensionPixelSize(resourceId)
}

fun getNavBarHeight(): Int {
    val res = Resources.getSystem()
    val resourceId = res.getIdentifier("navigation_bar_height", "dimen", "android")
    return if (resourceId != 0) res.getDimensionPixelSize(resourceId) else 0
}

inline fun TextView.onAfterTextChanged(crossinline action: (text: Editable?) -> Unit) {
    (getTag(textWatcherTagId) as? TextWatcher)?.let { oldWatcher ->
        removeTextChangedListener(
            oldWatcher
        )
    }
    val watcher = addTextChangedListener(afterTextChanged = action)
    this.setTag(textWatcherTagId, watcher)
}

fun TextView.setCompoundDrawable(
    left: Int? = null,
    top: Int? = null,
    right: Int? = null,
    bottom: Int? = null
) {
    val leftDrawable = if (left != null) ContextCompat.getDrawable(context, left) else null
    val topDrawable = if (top != null) ContextCompat.getDrawable(context, top) else null
    val rightDrawable = if (right != null) ContextCompat.getDrawable(context, right) else null
    val bottomDrawable = if (bottom != null) ContextCompat.getDrawable(context, bottom) else null
    setCompoundDrawablesWithIntrinsicBounds(
        leftDrawable,
        topDrawable,
        rightDrawable,
        bottomDrawable
    )
}

/*fun Context.createPopupWindow(layoutId: Int, touchOutsideDismiss: Boolean = true): PopupWindowExt {
    val window = PopupWindowExt(
        View.inflate(this, layoutId, null),
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    if (touchOutsideDismiss) {
        window.isFocusable = true
        window.isOutsideTouchable = true
    } else {
        window.isFocusable = false
        window.isOutsideTouchable = false
    }
    window.setBackgroundDrawable(BitmapDrawable())
    window.contentView.measure(0, 0)
    return window
}*/

fun PopupWindow.show(
    anchorView: View,
    xOffset: Int,
    yOffset: Int,
    gravity: Int = Gravity.NO_GRAVITY
) = apply {
    isFocusable = false
    showAsDropDown(anchorView, xOffset, yOffset, gravity)
    contentView.systemUiVisibility = HIDE_UI_FLAG
    isFocusable = true
    update()
}

/**
 * 自动根据 anchorView 选择位置弹出 PopupWindow，避开空间不足的问题
 */
fun PopupWindow.autoLocateShow(anchorView: View) {
    val anchorLoc = IntArray(2)
    anchorView.getLocationOnScreen(anchorLoc)
    val anchorHeight = anchorView.height
    val anchorWidth = anchorView.width

    val screenHeight = anchorView.context.getScreenHeight()
    val screenWidth = anchorView.context.getScreenWidth()

    contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
    val popHeight = contentView.measuredHeight
    val popWidth = contentView.measuredWidth

    val popLoc = IntArray(2)
    val needShowUp = screenHeight - anchorLoc[1] - anchorHeight < popHeight
    if (needShowUp) popLoc[1] = anchorLoc[1] - popHeight else popLoc[1] =
        anchorLoc[1] + anchorHeight

    popLoc[0] = when {
        anchorLoc[0] + anchorWidth / 2 < popWidth / 2 -> 0
        screenWidth - anchorLoc[0] - anchorWidth / 2 < popWidth / 2 -> screenWidth - popWidth
        else -> anchorLoc[0] + anchorWidth / 2 - popWidth / 2
    }
    showAtLocation(anchorView, Gravity.NO_GRAVITY, popLoc[0], popLoc[1])
}

fun Context.createPopupMenu(
    anchorView: View,
    menuId: Int,
    gravity: Int = Gravity.START,
    block: Menu.() -> Unit = {}
): PopupMenu {
    val popupMenu = PopupMenu(this, anchorView, gravity)
    popupMenu.inflate(menuId)
    block(popupMenu.menu)
    return popupMenu
}

//class PopupWindowExt(override val containerView: View, width: Int, height: Int) : PopupWindow(containerView, width, height), LayoutContainer

fun getStatusBarDpHeight(): Int {
    val statBarHeight = Resources.getSystem().getDimensionPixelSize(
        Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android")
    )
    val scale = CommonApp.obtain<App>().resources.displayMetrics.density
    return (statBarHeight / scale).toInt()
}