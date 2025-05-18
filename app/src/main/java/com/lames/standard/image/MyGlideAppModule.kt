package com.lames.standard.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.lames.standard.entity.OssUrlWrapper
import java.io.InputStream

/**
 * Created by Jason
 */
@GlideModule
class MyGlideAppModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(OssUrlWrapper::class.java, InputStream::class.java, OssUrlLoaderFactory())
    }
}