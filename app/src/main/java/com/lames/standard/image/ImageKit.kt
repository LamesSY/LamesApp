package com.lames.standard.image

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.lames.standard.entity.OssUrlWrapper

/**
 * Created by Jason
 */
object ImageKit {
    fun with(view: View): GlideRequests = GlideApp.with(view)
    fun with(context: Context): GlideRequests = GlideApp.with(context)
    fun with(activity: Activity): GlideRequests = GlideApp.with(activity)
    fun with(fragment: Fragment): GlideRequests = GlideApp.with(fragment)
    fun with(fragment: FragmentActivity): GlideRequests = GlideApp.with(fragment)

    fun GlideRequests.loadOssUrl(url: String?): GlideRequest<Drawable> =
        if (url.isNullOrEmpty()) load(url) else load(OssUrlWrapper(url))
}


