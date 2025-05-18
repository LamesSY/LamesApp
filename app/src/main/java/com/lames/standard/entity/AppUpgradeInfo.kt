package com.lames.standard.entity

data class AppUpgradeInfo(
    val versionName: String,
    val versionCode: Long,
    val downloadUrl: String,
    val updateContent: String,
)