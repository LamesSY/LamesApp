package com.lames.standard.mmkv

import com.lames.standard.common.GlobalVar
import com.lames.standard.entity.User
import com.lames.standard.tools.parseToJson
import com.lames.standard.tools.parseToObject

object UserMMKV {

    private var userJson by GlobalVar.obtain().userMMKV.string("key_user")
    var user: User? = null
        get() {
            return if (field == null) try {
                field = parseToObject<User>(userJson ?: return null)
                field
            } catch (e: Exception) {
                null
            } else field
        }
        set(value) {
            userJson = if (value != null) parseToJson(value) else null
            field = value
        }


    fun clear() {
        user = null
    }
}