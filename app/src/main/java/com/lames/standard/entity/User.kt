package com.lames.standard.entity

import com.google.gson.annotations.SerializedName
import com.lames.standard.R
import com.lames.standard.tools.changeTimeToStamp
import com.lames.standard.tools.forString

data class User(
    var uid: String,
    val token: String,
    var userName: String?,
    var nickName: String?,
    var relation: String?,
    var phone: String?,
    var email: String?,
    var emailCertTime: String?,
    var emailStatus: Int,
    var idCard: String?,
    var sex: Int,                   //0：女 1：男 2：未知
    var birthday: String?,
    var age: Int,
    var avatar: String?,
    var wearState: Int?,            //佩戴状态：0无设备 1在线 2离线
    var height: Double,             //身高：cm
    var weight: Double,             //体重：kg
    var m62: Double,                //腰围：cm
    var sleepTarget: Int,           //睡眠目标：分钟
    var stepTarget: Int,            //步数目标：步
    var calorieTarget: Int,         //消耗热量目标：卡
    var distanceTarget: Int,        //运动距离目标：米
) {
    val isInfoFinish get() = birthday != null && height > 0 && weight > 0

    val sexStr get() = if (sex == 0) forString(R.string.female) else forString(R.string.male)

    val ageF
        get() = runCatching {
            val t = changeTimeToStamp(birthday!!, "yyyy-MM-dd")
            (System.currentTimeMillis() - t) / 1000 / (365 * 24 * 60 * 60f)
        }.getOrNull() ?: 0f

    fun getAvatar() = if (avatar.isNullOrEmpty()) {
        R.mipmap.img_default_avatar
    } else avatar

    fun updateInfo(user: User) {
        uid = user.uid
        userName = user.userName
        nickName = user.nickName
        relation = user.relation
        phone = user.phone
        email = user.email
        emailCertTime = user.emailCertTime
        emailStatus = user.emailStatus
        idCard = user.idCard
        sex = user.sex
        birthday = user.birthday
        age = user.age
        avatar = user.avatar
        wearState = user.wearState
        height = user.height
        weight = user.weight
        m62 = user.m62
        sleepTarget = user.sleepTarget
        stepTarget = user.stepTarget
        calorieTarget = user.calorieTarget
        distanceTarget = user.distanceTarget
    }
}

/**
 * 用户健康基本信息
 */
data class UserBasicInfo(
    val birthday: String,
    val bmi: Double,
    val height: Double,
    val m62: Double,
    val sex: Int,
    val sleepTarget: Int,
    val stepTarget: Int,
    val uid: String,
    val weight: Double,
    var calorieTarget: Int,
    var distanceTarget: Int,
    var targetFlag: Int, //1表示已设置
    @SerializedName("county")
    var country: Long,
    @SerializedName("countyName")
    var countryName: String?,
    var pro: Long,
    var proName: String?,
    var city: Long,
    var cityName: String?,
    var cardType: Int,
    var idCard: String?,
    var qrCode: String?,
)