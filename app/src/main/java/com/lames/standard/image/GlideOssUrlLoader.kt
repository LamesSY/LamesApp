package com.lames.standard.image

import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader
import com.lames.standard.entity.OssUrlWrapper
import java.io.InputStream

/**
 * Created by Jason
 */
class OssUrlLoader(concreteLoader: ModelLoader<GlideUrl, InputStream>?) :
    BaseGlideUrlLoader<OssUrlWrapper>(concreteLoader) {

    override fun getUrl(model: OssUrlWrapper, width: Int, height: Int, options: Options?): String {
        val url = model.url
        return when {
            url.endsWith(
                ".mp4",
                true
            ) -> "$url?x-oss-process=video/snapshot,t_5000,f_jpg,w_${width},h_0,m_fast"

            url.endsWith(".gif", true) -> url
            else -> "$url?x-oss-process=image/resize,s_${width}"
        }
    }

    override fun handles(model: OssUrlWrapper): Boolean = true
}

class OssUrlLoaderFactory : ModelLoaderFactory<OssUrlWrapper, InputStream> {
    override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<OssUrlWrapper, InputStream> {
        return OssUrlLoader(multiFactory.build(GlideUrl::class.java, InputStream::class.java))
    }

    override fun teardown() {}
}