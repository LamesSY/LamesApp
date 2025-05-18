package com.lames.standard.tools

import java.text.DecimalFormat

/**
 * Created by Jason
 */
/**
 * 判断条件后，true 则执行或返回 value 否则返回空
 */
infix fun <T> Boolean.then(value: T) = if (this) value else null

/**
 * 判断为空和不为空的逻辑
 */
inline fun <T : Any> T?.isNotNull(notNullAction: (T) -> Unit, nullAction: () -> Unit = {}) {
    if (this != null) notNullAction(this) else nullAction.invoke()
}

/**
 * 判断为空和不为空的逻辑
 */
inline fun <T : Any> T?.isNull(nullAction: () -> Unit, notNullAction: (T) -> Unit = {}) {
    if (this == null) nullAction.invoke() else notNullAction(this)
}

/**
 * 数字类是否为空或者为0
 */
fun Number?.isNullOrZero(): Boolean = (this == null || this == 0)

/**
 * 数字类是否为空或者为某个传入的值
 */
fun Number?.isNullOr(num: Number): Boolean = (this == null || this.toDouble() == num.toDouble())

/**
 * 当满足条件时执行 action
 */
inline fun takeActionWhen(enable: Boolean, action: () -> Unit) {
    if (enable) action()
}

val number1Formatter = DecimalFormat("######0.0")
val number2Formatter = DecimalFormat("######0.00")

fun Double?.round(digits: Int = 1): String {
    this ?: return if (digits == 1) "0.0" else "0.00"
    return if (digits == 1) number1Formatter.format(this) else number2Formatter.format(this)
}

fun Float?.round(digits: Int = 1): String {
    this ?: return if (digits == 1) "0.0" else "0.00"
    return if (digits == 1) number1Formatter.format(this) else number2Formatter.format(this)
}

fun Int.formatLargeNum(): String {
    return when {
        this < 1000 -> this.toString()
        this <= 100000 -> "${(this / 1000.0).round()}k"
        else -> "${(this / 10000.0).round()}w"
    }
}
