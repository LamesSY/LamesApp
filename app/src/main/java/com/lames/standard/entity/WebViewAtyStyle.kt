package com.lames.standard.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WebViewAtyStyle(
    var title: String = "",
    var barStyle: Int = 0,
) : Parcelable