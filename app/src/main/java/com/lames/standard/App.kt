package com.lames.standard

import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.lames.standard.common.CommonApp
import com.lames.standard.common.GlobalVar
import com.lames.standard.event.CrashHandler
import com.lames.standard.event.LogEventKit
import com.lames.standard.mmkv.UserMMKV
import com.lames.standard.network.HttpKit
import com.lames.standard.push.NotificationHelper
import com.lames.standard.push.PushHelp
import com.lames.standard.tools.AppKit
import com.lames.standard.tools.ToastKit
import com.lames.standard.webx.WebViewPool
import kotlin.math.min

class App : CommonApp() {

    override fun doCommonInit() {
        initHttp()
        ToastKit.init(this)
        GlobalVar.obtain()
        CrashHandler.obtain().init()
        LogEventKit.obtain().init(this)
        NotificationHelper.initAppNotificationChannel()
        AppKit.obtain().init(this)
        WebViewPool.getInstance().setMaxPoolSize(min(Runtime.getRuntime().availableProcessors(), 6))
        WebViewPool.getInstance().init(this)
        initRefresh()
    }

    private fun initHttp() {
        HttpKit.initHttpConfig()
    }

    private fun initRefresh() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            layout.setPrimaryColorsId(R.color.transparent);//全局设置主题颜色
            return@setDefaultRefreshHeaderCreator ClassicsHeader(context);
        }

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            return@setDefaultRefreshFooterCreator ClassicsFooter(context).setDrawableSize(20f)
        }
    }

    override fun onSignOut() {
        PushHelp.unbindAccount(UserMMKV.user?.uid)
        NotificationHelper.cancelAll()
        UserMMKV.clear()
        GlobalVar.obtain().userMMKV.clear()
        AppKit.obtain().restartApp(this)
    }
}