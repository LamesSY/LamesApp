@file:JvmName("ResKit")

package com.lames.standard.tools

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.TypedValue
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Created by Jason
 */
fun Context.strRes(@StringRes resId: Int): String = getString(resId)

fun Fragment.strRes(@StringRes resId: Int): String = activity?.strRes(resId) ?: ""

fun Context.colorRes(@ColorRes resId: Int): Int = ContextCompat.getColor(this, resId)

fun Fragment.colorRes(@ColorRes resId: Int): Int =
    activity?.colorRes(resId) ?: Color.parseColor("#FFFFFFFF")

fun Context.drawable(@DrawableRes resId: Int): Drawable = ContextCompat.getDrawable(this, resId)!!

fun Fragment.drawable(@DrawableRes resId: Int): Drawable? = activity?.drawable(resId)

fun forAttrColor(context: Context, attrId: Int): Int {
    val typedValue = TypedValue()
    context.theme.resolveAttribute(attrId, typedValue, true)
    return ContextCompat.getColor(context, typedValue.resourceId)
}