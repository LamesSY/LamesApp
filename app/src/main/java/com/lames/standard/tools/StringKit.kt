@file:JvmName("StringKit")

package com.lames.standard.tools

import java.util.regex.Pattern

/**
 * Created by Jason
 */

/**
 * 组合字符串，并添加分隔符内容
 */
fun combineString(divider: String, vararg fragments: String?): String {
    return if (fragments.isNullOrEmpty()) ""
    else fragments.reduce { acc, s -> if (s.isNullOrEmpty()) acc else "$acc$divider$s" } ?: ""
}

/**
 * 组合字符串，并添加分隔符内容
 */
fun combineString(divider: String, stringList: MutableList<String>?): String {
    return if (stringList.isNullOrEmpty()) ""
    else stringList.reduce { acc, s -> if (s.isEmpty()) acc else "$acc$divider$s" }
}

/**
 * 是否纯数字
 */
fun String?.isDigital(): Boolean = this != null && this.matches(Regex("[0-9]+"))

/**
 * 是否为数值
 */
fun String?.isNumber(): Boolean =
    this != null && this.matches(Regex("\\d+||\\d*\\.\\d+||\\d*\\.?\\d+?e[+-]\\d*\\.?\\d+?||e[+-]\\d*\\.?\\d+?"))

/**
 * 是否为中国一代身份证
 * 假设15位身份证号码:410001910101123  410001 910101 123
 * ^开头
 * [1-9] 第一位1-9中的一个      4
 * \\d{5} 五位数字           10001（前六位省市县地区）
 * \\d{2}                    91（年份）
 * ((0[1-9])|(10|11|12))     01（月份）
 * (([0-2][1-9])|10|20|30|31)01（日期）
 * \\d{3} 三位数字            123（第十五位奇数代表男，偶数代表女），15位身份证不含X
 * $结尾
 */
fun String?.isFirstGenIdNum(): Boolean {
    return this?.matches(Regex("(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)")) == true
}

/**
 * 是否为中国二代身份证
 * ^开头
 * [1-9] 第一位1-9中的一个      4
 * \\d{5} 五位数字           10001（前六位省市县地区）
 * (18|19|20)                19（现阶段可能取值范围18xx-20xx年）
 * \\d{2}                    91（年份）
 * ((0[1-9])|(10|11|12))     01（月份）
 * (([0-2][1-9])|10|20|30|31)01（日期）
 * \\d{3} 三位数字            123（第十七位奇数代表男，偶数代表女）
 * [0-9Xx] 0123456789Xx其中的一个 X（第十八位为校验值）
 * $结尾
 */
fun String?.isSecondGenIdNum(): Boolean {
    return this?.matches(Regex("(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|")) == true
}

/**
 * 是否为中国大陆地区手机号码
 * 支持13、14、15、16、17、18、19开头后面任意搭9位
 */
fun String?.isPhone(): Boolean {
    return this?.length == 11 && this.startsWith("1")
    //return this?.matches(Regex("^(13|14|15|16|17|18|19)\\d{9}$")) == true
}

/**
 * 是否为正确ip地址，也可以用于判断掩码等
 */
fun String?.isValidIp(): Boolean {
    return this?.matches(Regex("(2(5[0-5]|[0-4]\\d)|[0-1]?\\d{1,2})(\\.(2(5[0-5]|[0-4]\\d)|[0-1]?\\d{1,2})){3}")) == true
}

/**
 * 判断是否为 email 地址
 * true 表示当前是 email
 */
fun String?.isMail(): Boolean {
    val flag: Boolean = try {
        val check = "^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$"
        val regex = Pattern.compile(check)
        val matcher = regex.matcher(this ?: return false)
        matcher.matches()
    } catch (e: Exception) {
        false
    }
    return flag
}