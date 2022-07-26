package com.vpn.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.vpn.utils.SkinUtils

open class SetActionBarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setStatusBarColor()
        super.onCreate(savedInstanceState)
        setActionBar()
    }

    private fun setActionBar() {
        if (supportActionBar != null) { // 因为有的activity没有actionBar，所以加个判断
            val skin: SkinUtils.Skin = SkinUtils.getSkin(this)
            supportActionBar?.setBackgroundDrawable(ColorDrawable(skin.primaryColor))
            if (Build.VERSION.SDK_INT >= 21) {    //兼容5.0  去除actionbar阴影
                supportActionBar?.elevation = 0f
            }
        }
    }

    /**
     * 沉浸式状态栏
     */
    fun setStatusBarColor() {
        val skin: SkinUtils.Skin = SkinUtils.getSkin(this)
        setStatusBarLight(skin.isLight)
    }

    private fun setStatusBarLight(light: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // >=5.0 背景为全透明
            /* >=5.0，this method(getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS));
            in some phone is half-transparent like vivo、nexus6p..
            in some phone is full-transparent
            so ...*/
            val window: Window = getWindow()
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (light) {
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
                } else {
                    window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                }
                window.statusBarColor = Color.TRANSPARENT
            } else {
                window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
                if (light) {
                    window.statusBarColor = Color.BLACK
                } else {
                    window.statusBarColor = Color.TRANSPARENT
                }
            }
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 4.4背景为渐变半透明
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }
}