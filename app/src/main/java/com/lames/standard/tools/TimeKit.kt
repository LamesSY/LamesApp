@file:JvmName("TimeKit")

package com.lames.standard.tools

import java.text.SimpleDateFormat
import java.util.*

private val patternPool = hashMapOf<String, SimpleDateFormat>()
private val tempCalendar = Calendar.getInstance()

fun changeTimeToStamp(parseTime: String, pattern: String): Long {
    return try {
        val parse = SimpleDateFormat(pattern, Locale.getDefault()).parse(parseTime)
        parse?.time ?: 0
    } catch (e: Exception) {
        e.printStackTrace()
        0
    }
}

var Calendar.year: Int
    get() = get(Calendar.YEAR)
    set(value) = set(Calendar.YEAR, value)

var Calendar.month: Int
    get() = get(Calendar.MONTH)
    set(value) = set(Calendar.MONTH, value)

var Calendar.dayOfMonth: Int
    get() = get(Calendar.DAY_OF_MONTH)
    set(value) = set(Calendar.DAY_OF_MONTH, value)

var Calendar.dayOfWeek: Int
    get() = get(Calendar.DAY_OF_WEEK)
    set(value) = set(Calendar.DAY_OF_WEEK, value)

var Calendar.dayOfYear: Int
    get() = get(Calendar.DAY_OF_YEAR)
    set(value) = set(Calendar.DAY_OF_YEAR, value)

var Calendar.hourOfDay: Int
    get() = get(Calendar.HOUR_OF_DAY)
    set(value) = set(Calendar.HOUR_OF_DAY, value)

var Calendar.minute: Int
    get() = get(Calendar.MINUTE)
    set(value) = set(Calendar.MINUTE, value)

var Calendar.second: Int
    get() = get(Calendar.SECOND)
    set(value) = set(Calendar.SECOND, value)

/**
 * 为 Calendar 设置年月日
 */
fun Calendar.setDate(year: Int, monthFromZero: Int, day: Int): Calendar {
    this.year = year
    this.month = monthFromZero
    this.dayOfMonth = day
    return this
}

/**
 * 将某个成员设置为最大，如月份最后一天，一周的最后一日
 */
fun Calendar.setToMax(field: Int): Calendar {
    this.set(field, this.getMaximum(field))
    return this
}

/**
 * 将某个成员设置为最小，如一个月份的第一天，一周的开始那一天
 */
fun Calendar.setToMin(field: Int): Calendar {
    this.set(field, this.getMinimum(field))
    return this
}

/**
 * 日历中的时间按照格式输出字符串
 */
fun Calendar.toPatternString(pattern: String): String {
    return this.timeInMillis.toPatternString(pattern)
}

/**
 * 将格式化的时间文本按照既定格式设置到 Calendar 中
 * @param pattern 时间格式文本，如 yyyy-MM-dd
 * @param formattedTime 待设置的格式化时间，如 2023-03-03
 */
fun Calendar.setTimeFromFormattedString(pattern: String, formattedTime: String) {
    var formatter = patternPool[pattern]
    if (formatter == null) {
        formatter = SimpleDateFormat(pattern, Locale.getDefault())
        patternPool[pattern] = formatter
    }
    val dateTime = formatter.parse(formattedTime) ?: throw NullPointerException()
    this.time = dateTime
}

/**
 * 对比另一个 Calendar，是否表示同一天
 */
fun Calendar.isSameDay(other: Calendar) =
    year == other.year && get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

/**
 * 对比另一个 Calendar，是否表示同一周
 */
fun Calendar.isSameWeek(other: Calendar) =
    year == other.year && get(Calendar.WEEK_OF_YEAR) == other.get(Calendar.WEEK_OF_YEAR)

/**
 * 对比另一个 Calendar，是否表示同一个月
 */
fun Calendar.isSameMonth(other: Calendar) =
    year == other.year && get(Calendar.MONTH) == other.get(Calendar.MONTH)

/**
 * 为当前 Calendar 设置时分秒时间
 */
fun Calendar.setTime(hourOfDay: Int, minute: Int, second: Int = 0, milliSec: Int = 0): Calendar {
    this.set(Calendar.HOUR_OF_DAY, hourOfDay)
    this.set(Calendar.MINUTE, minute)
    this.set(Calendar.SECOND, second)
    this.set(Calendar.MILLISECOND, milliSec)
    return this
}

/**
 * 设置当前日历到当天最小的时间戳
 */
fun Calendar.startTimeInMillisOfToday(): Long {
    tempCalendar.timeInMillis = this.timeInMillis
    tempCalendar.setTime(0, 0, 0, 0)
    return tempCalendar.timeInMillis
}

/**
 * 设置当前日历到当天最大的时间戳
 */
fun Calendar.endTimeInMillisOfToday(): Long {
    tempCalendar.timeInMillis = this.timeInMillis
    tempCalendar.setTime(0, 0, 0, 0)
    tempCalendar.add(Calendar.DAY_OF_MONTH, 1)
    return tempCalendar.timeInMillis
}

/**
 * 格式化 Long 日期
 */
fun Long.toPatternString(formatPattern: String): String = Date(this).toPatternString(formatPattern)

/**
 * 格式化 Date 日期
 */
fun Date.toPatternString(formatPattern: String): String {
    var dateFormat = patternPool[formatPattern]
    if (dateFormat == null) {
        dateFormat = SimpleDateFormat(formatPattern, Locale.getDefault())
        patternPool[formatPattern] = dateFormat
    }
    return dateFormat.format(this)
}

/**
 * 将格式化好的字符串按照目标格式转换
 */
@Throws(Exception::class)
fun String.convertPattern(sourcePattern: String, destPattern: String): String {
    var sourceDateFormat = patternPool[sourcePattern]
    if (sourceDateFormat == null) {
        sourceDateFormat = SimpleDateFormat(sourcePattern, Locale.getDefault())
        patternPool[sourcePattern] = sourceDateFormat
    }
    val sourceDate = sourceDateFormat.parse(this) ?: throw NullPointerException()
    return sourceDate.toPatternString(destPattern)
}