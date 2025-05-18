package com.lames.standard.push

import android.content.Context
import com.alibaba.sdk.android.push.AliyunMessageIntentService
import com.alibaba.sdk.android.push.notification.CPushMessage
import com.google.gson.Gson
import com.lames.standard.common.Constants.Project.EMPTY_STR
import com.lames.standard.tools.LogKit

class PushMsgService : AliyunMessageIntentService() {

    private val TAG = this::class.java.simpleName

    /**
     * 点击通知回调。点击通知会回调该方法。
     */
    override fun onNotificationOpened(p0: Context?, p1: String?, p2: String?, p3: String?) {
        LogKit.d(TAG, "点击通知,p1:$p1,p2:$p2,p3:$p3")
    }

    /**
     * 删除通知的回调。删除通知时会回调该方法。
     */
    override fun onNotificationRemoved(p0: Context?, p1: String?) {
        LogKit.d(TAG, "删除通知,p1:$p1")
    }

    /**
     * 推送通知到达回调。SDK收到通知后，回调该方法，可获取到并处理通知相关的参数。
     */
    override fun onNotification(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?
    ) {
        LogKit.d(TAG, "通知到达,p1:$p1,p2:$p2,p3:$p3")
    }

    /**
     * 是否立即显示通知，此方法用于控制是否将推送通知交给SDK默认处理还是由用户自行处理。如果返回false，则您需要在onNotificationReceivedInApp内处理该推送通知。
     */
    override fun showNotificationNow(p0: Context?, p1: MutableMap<String, String>?): Boolean {
        LogKit.d(TAG, "是否立即显示通知,p1:${Gson().toJson(p1)}")
        return false
    }

    /**
     * 用于接收服务端推送的消息，消息不会弹窗，而是回调该方法。
     */
    override fun onMessage(p0: Context?, p1: CPushMessage?) {
        LogKit.d(TAG, "服务端推送的消息,p0:$p0,p1:${Gson().toJson(p1)}")
    }

    /**
     * 点击无跳转逻辑通知的回调，点击无跳转逻辑（open=4）通知时回调该方法（v2.3.2及以上版本支持）。
     */
    override fun onNotificationClickedWithNoAction(
        p0: Context?,
        p1: String?,
        p2: String?,
        p3: String?
    ) {
        LogKit.d(TAG, "点击无跳转逻辑通知,p1:$p1,p2:$p2,p3:$p3")
    }

    /**
     * 通知在应用内回调，该方法仅在showNotificationNow返回false时才会被回调，且此时不调用onNotification，此时需要您自己处理通知逻辑。
     */
    override fun onNotificationReceivedInApp(
        context: Context,
        p1: String?,
        p2: String?,
        p3: MutableMap<String, String>?,
        p4: Int,
        p5: String?,
        p6: String?
    ) {
        LogKit.d(TAG, "通知在应用内回调,p1:$p1,p2:$p2,p3:$p3,p4:$p4,p5:$p5,p6:$p6")
        NotificationHelper.showNotification(
            context,
            title = p1 ?: EMPTY_STR,
            content = p2 ?: EMPTY_STR,
            id = (99..999999).random(),
            channelId = NotificationHelper.LEVEL_HIGH
        )
    }

}