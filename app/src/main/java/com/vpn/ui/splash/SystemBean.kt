package com.vpn.ui.splash

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName


@Keep data class SystemBean(
    @SerializedName("url") val appH5Url: String, // http://test.mvs.ac/#/index
    @SerializedName("status") var isOpenH5: Int, // 1
    @SerializedName("name") val name: String, // 1
    @SerializedName("surl") val sUrl: String, // 1

)