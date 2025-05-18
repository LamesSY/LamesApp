package com.lames.standard.image

import androidx.annotation.NonNull
import com.bumptech.glide.annotation.GlideExtension
import com.bumptech.glide.annotation.GlideOption
import com.bumptech.glide.request.BaseRequestOptions
import com.lames.standard.R

/**
 * Created by Jason
 */
@GlideExtension
class MyGlideExtension private constructor() {

    companion object {
        @JvmStatic
        @GlideOption
        fun avatar(@NonNull options: BaseRequestOptions<*>): BaseRequestOptions<*> =
            options.error(R.drawable.ic_glide_placeholder)
                .placeholder(R.drawable.ic_glide_placeholder).override(200, 200)

        @JvmStatic
        @GlideOption
        fun placeHolder(@NonNull options: BaseRequestOptions<*>): BaseRequestOptions<*> =
            options.error(R.drawable.ic_glide_placeholder)
                .placeholder(R.drawable.ic_glide_placeholder)
    }
}