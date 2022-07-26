package com.vpn.base

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import androidx.appcompat.widget.Toolbar
import com.vpn.utils.SkinUtils

/**
 * Created by Administrator on 2017/12/14 0014.
 * 兼容沉浸式状态栏
 */
class VpnToolbar @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0 ) : Toolbar( context!!, attrs, defStyleAttr )
{
    private fun setup() {
        var mCompatPaddingTop = 0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCompatPaddingTop = statusHeight
        }
        setPadding(paddingLeft, paddingTop + mCompatPaddingTop, paddingRight, paddingBottom)
        // 更换Toolbar背景颜色
        setBackgroundColor(SkinUtils.getSkin(context).primaryColor)
    }

    private val statusHeight: Int
        private get() {
            var statusBarHeight = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusBarHeight = resources.getDimensionPixelSize(resourceId)
            }
            return statusBarHeight
        }

    private fun px2dp(pxVal: Float): Float {
        val scale = context.resources.displayMetrics.density
        return pxVal / scale
    }

    init {
        setup()
    }
}