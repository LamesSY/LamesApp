package com.lames.standard.tools

import android.text.InputFilter
import com.lames.standard.common.Constants
import java.util.regex.Pattern

object EditFilter {

    val userNameLimit get() = arrayOf(signFilter, emojiFilter, limit20)

    val signFilter = InputFilter { source, i, i2, spanned, i3, i4 ->
        val speChat = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？_-]"
        val pattern = Pattern.compile(speChat)
        val matcher = pattern.matcher(source.toString())
        if (matcher.find()) return@InputFilter ""
        else return@InputFilter null;
    }

    val emojiFilter = InputFilter { p0, p1, p2, spanned, i3, i4 ->
        for (i in p1 until p2) {
            val type = Character.getType(p0[i])
            if (type == Character.SURROGATE.toInt() || type == Character.OTHER_SYMBOL.toInt()) {
                return@InputFilter Constants.Project.EMPTY_STR
            }
        }
        return@InputFilter null
    }

    val limit50 = InputFilter.LengthFilter(50)

    val limit20 = InputFilter.LengthFilter(20)

    val limit10 = InputFilter.LengthFilter(10)
}