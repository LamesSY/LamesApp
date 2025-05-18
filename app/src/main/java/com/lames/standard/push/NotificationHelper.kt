package com.lames.standard.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import com.lames.standard.App
import com.lames.standard.R
import com.lames.standard.common.CommonApp

object NotificationHelper {

    private val notificationManager: NotificationManager by lazy {
        CommonApp.obtain<App>().getSystemService()!!
    }

    const val LEVEL_HIGH = "high"
    const val LEVEL_DEFAULT = "default"
    const val LEVEL_LOW = "low"

    fun showNotification(
        context: Context,
        title: String,
        content: String,
        id: Int,
        intent: PendingIntent? = null,
        channelId: String = LEVEL_DEFAULT,
        onGoing: Boolean = false,
        autoCancel: Boolean = true,
        needNotify: Boolean = true,
    ): Notification {
        var priority = NotificationCompat.PRIORITY_MAX
        var defaultOption = Notification.DEFAULT_ALL
        when (channelId) {
            LEVEL_HIGH -> {
                priority = NotificationCompat.PRIORITY_MAX
                defaultOption = Notification.DEFAULT_ALL //所有
            }

            LEVEL_DEFAULT -> {
                priority = NotificationCompat.PRIORITY_DEFAULT
                defaultOption = Notification.DEFAULT_SOUND //声音
            }

            LEVEL_LOW -> {
                priority = NotificationCompat.PRIORITY_DEFAULT
                defaultOption = Notification.DEFAULT_LIGHTS //呼吸灯
            }
        }
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val build = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(content) //内容
                .setContentIntent(intent)
                .setAutoCancel(autoCancel)
                .setOngoing(onGoing)
                .setDefaults(defaultOption)
                .build()
            if (needNotify) {
                notificationManager.notify(id, build)
            }
            build
        } else {
            val build = NotificationCompat.Builder(context)
                .setPriority(priority)
                .setDefaults(defaultOption)
                .setSmallIcon(R.mipmap.ic_app_logo)
                .setContentTitle(title)
                .setTicker(title)
                .setContentText(content) //内容
                .setContentIntent(intent)
                .setAutoCancel(autoCancel)
                .setOngoing(onGoing)
                .build()
            if (needNotify) {
                notificationManager.notify(id, build)
            }
            build
        }
    }

    fun initAppNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                LEVEL_HIGH,
                "important",
                NotificationManagerCompat.IMPORTANCE_HIGH
            )
            createNotificationChannel(
                LEVEL_DEFAULT,
                "default",
                NotificationManagerCompat.IMPORTANCE_DEFAULT
            )
            createNotificationChannel(LEVEL_LOW, "others", NotificationManagerCompat.IMPORTANCE_LOW)
        }
    }

    fun isNotificationEnable(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    fun openNotificationSetting() {
        val intent = Intent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, CommonApp.obtain<App>().packageName)
        } else {
            intent.action = "android.settings.APP_NOTIFICATION_SETTINGS"
            intent.putExtra("app_package", CommonApp.obtain<App>().packageName)
            intent.putExtra("app_uid", CommonApp.obtain<App>().applicationInfo.uid)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        CommonApp.obtain<App>().startActivity(intent)
    }

    fun cancel(context: Context, id: Int) {
        notificationManager.cancel(id)
    }

    fun cancelAll() = notificationManager.cancelAll()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(id: String, name: String, importanceLevel: Int) {
        val channel = NotificationChannel(id, name, importanceLevel)
        notificationManager.createNotificationChannel(channel)
    }
}