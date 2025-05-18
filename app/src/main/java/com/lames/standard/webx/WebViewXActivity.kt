package com.lames.standard.webx

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import com.lames.standard.common.CommonActivity
import com.lames.standard.common.Constants
import com.lames.standard.databinding.ActivityContainerBinding

class WebViewXActivity : CommonActivity<ActivityContainerBinding>() {

    private val url by lazy { intent.getStringExtra(Constants.Params.ARG1) }
    private val title by lazy { intent.getStringExtra(Constants.Params.ARG2) }

    /**
     * 标题栏样式 0默认样式 1展开式 2透明
     */
    private val barStyle by lazy { intent.getIntExtra(Constants.Params.ARG3, 0) }

    override fun getViewBinding() = ActivityContainerBinding.inflate(LayoutInflater.from(this))

    override fun initialization() {
        val args = bundleOf(Constants.Params.ARG1 to url, Constants.Params.ARG2 to title)
        supportFragmentManager.commit {
            if (barStyle == 2) add(binding.fcView.id, WebTranViewXFragment::class.java, args)
            else add(binding.fcView.id, WebViewXFragment::class.java, args)
        }
    }

    companion object {
        fun start(context: Context, url: String, title: String? = null, barStyle: Int = 0) {
            val intent = Intent(context, WebViewXActivity::class.java)
            intent.putExtra(Constants.Params.ARG1, url)
            intent.putExtra(Constants.Params.ARG2, title)
            intent.putExtra(Constants.Params.ARG3, barStyle)
            context.startActivity(intent)
        }
    }
}