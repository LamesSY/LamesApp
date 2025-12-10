package com.lames.standard.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * 微信用户信息
 */
@Parcelize
data class UserWXInfo(
    val avatar: String?,
    val nickName: String?,
    val openId: String,
    val register: Boolean,
    val sessionKey: String?,
    val unionid: String?,
) : Parcelable