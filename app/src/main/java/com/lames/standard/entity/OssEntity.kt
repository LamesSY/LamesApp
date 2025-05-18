package com.lames.standard.entity

import com.lames.standard.oss.OssKit

/**
 * 上传到 OSS 的token
 */
data class OssToken(
    var status: String?,
    var accessKeySecret: String?,
    var securityToken: String?,
    var accessKeyId: String?,
    var expiration: String?
) {
    val isNearExpiration: Boolean
        get() {
            val expiration = expiration ?: return true
            val currentTime = System.currentTimeMillis()
            try {
                val tokenExpiration =
                    OssKit.TIME_FORMAT.parse(expiration.replace("Z", " UTC"))?.time ?: 0
                if (currentTime > tokenExpiration || tokenExpiration - currentTime < 5 * 60 * 1000) return true
            } catch (e: Exception) {
                e.printStackTrace()
                return true
            }
            return false
        }
}