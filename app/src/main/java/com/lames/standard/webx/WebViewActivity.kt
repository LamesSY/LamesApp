package com.lames.standard.webx

import android.content.Context
import android.content.Intent
import androidx.core.os.bundleOf
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityContainerBinding
import com.lames.standard.entity.WebViewAtyStyle
import com.lames.standard.tools.loadFirstFragment

class WebViewActivity : CommonActivity<ActivityContainerBinding>() {

    private val url by lazy { intent.getStringExtra(Constants.Params.ARG1) ?: "" }
    private val wvAtyStyle by lazy { intent?.getParcelableExtra<WebViewAtyStyle>(Constants.Params.ARG2) ?: WebViewAtyStyle() }

    override fun getViewBinding() = ActivityContainerBinding.inflate(layoutInflater)

    override fun initialization() {
        val args = bundleOf(Constants.Params.ARG1 to url, Constants.Params.ARG2 to wvAtyStyle)
        loadFirstFragment(WebViewFragment::class.java, args)

    }

    companion object {
        const val BAR_NORMAL = 0
        const val BAR_TRANSPORT = 2
        const val BAR_REMOVE = 3
        fun start(context: Context, url: String, title: String? = null, barStyle: Int = 0) {
            val webviewStyle = WebViewAtyStyle()
            webviewStyle.title = title ?: ""
            webviewStyle.barStyle = barStyle

            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(Constants.Params.ARG1, url)
            intent.putExtra(Constants.Params.ARG2, webviewStyle)
            context.startActivity(intent)
        }
    }

}