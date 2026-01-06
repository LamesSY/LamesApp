package com.lames.standard.mmkv

import com.lames.standard.common.GlobalVar
import com.lames.standard.entity.User
import com.lames.standard.tools.parseToJson
import com.lames.standard.tools.parseToObject

object UserMMKV {

    private var userJson by GlobalVar.obtain().userMMKV.string("key_user")
    val user get() = runCatching { parseToObject<User>(userJson!!) }.getOrNull()
    fun setUserInfo(user: User) = runCatching { userJson = parseToJson(user) }

    fun clear() {
        userJson = null
    }
}