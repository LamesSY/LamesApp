package com.lames.standard.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebViewAtyStyle(
    /**0正常加载 2post**/
    var action: Int = 0,
    var title: String = "",
    var barStyle: Int = 0,
    var extraParams: String? = null,
) : Parcelable