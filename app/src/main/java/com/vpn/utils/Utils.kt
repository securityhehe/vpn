package com.vpn.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
import com.vpn.R

object Utils {
    @JvmStatic
    fun getImgURL(resourceId: Int): String? {

        // Use BuildConfig.APPLICATION_ID instead of R.class.getPackage().getName() if both are not same
        return Uri.parse("android.resource://" + R::class.java.getPackage().name + "/" + resourceId)
            .toString()
    }

    fun netCheck(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm?.activeNetworkInfo?.isConnectedOrConnecting ?: false
    }

     fun dip2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}