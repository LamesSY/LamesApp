package com.lames.standard.entity

data class NativeNavigateParams(
    /**
     * 打开原生页面所需的业务内容id，这个的值需要根据各个页面的具体含义
     */
    val id: Long = 0,

    /**
     * 需要展示哪个用户的内容
     */
    val uId: String?,

    val time: Long = 0,

    /**
     * 用于查询运动记录
     */
    val duid: String?,

    /**
     * 设备类型
     */
    val deviceModelDuid: String?,

    /**
     * 设备uuid
     */
    val deviceUuid: String?,
) {
    override fun toString(): String =
        "NativeNavigateParams(id=$id, uId=$uId, time=$time, duid=$duid)"
}