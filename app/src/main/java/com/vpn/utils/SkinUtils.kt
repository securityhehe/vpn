package com.vpn.utils

import android.content.Context
import com.vpn.utils.Stander.mmkv
import com.vpn.utils.SkinUtils.Skin
import com.vpn.R
import com.vpn.utils.SkinUtils
import com.vpn.utils.Stander
import android.content.res.ColorStateList
import android.graphics.Color
import java.util.*

object SkinUtils {
    var defaultSkins = Arrays.asList( // 绿色主题，白色标题绿色控件，
        Skin(R.string.theme_color, 0x282734, 0xFFA5C9, true)
    )

    // sk默认极简绿,
    // kx默认清水蓝,
    private val DEFAULT_SKIN = defaultSkins[0]
    private var currentSkin: Skin? = null
    private fun requireSkin(ctx: Context): Skin {
        if (currentSkin != null) {
            return currentSkin!!
        }
        synchronized(SkinUtils::class.java) {
            if (currentSkin == null) {
                val savedSkinColor =
                    mmkv().decodeInt(Constants.KEY_SKIN_NAME, DEFAULT_SKIN.hashCode())
                for (skin in defaultSkins) {
                    if (skin.hashCode() == savedSkinColor) {
                        currentSkin = skin
                        break
                    }
                }
                if (currentSkin == null) {
                    // 本地保存的皮肤数据出了异常，比如高版本删除了低版本存在的皮肤，
                    currentSkin = DEFAULT_SKIN
                }
            }
        }
        return currentSkin!!
    }

    fun getSkin(ctx: Context): Skin {
        return requireSkin(ctx)
    }

    fun setSkin(ctx: Context?, skin: Skin) {
        currentSkin = skin
        mmkv().encode(Constants.KEY_SKIN_NAME, skin.hashCode())
    }

    class Skin internal constructor(
        val colorName: Int,
        primaryColor: Int,
        accentColor: Int,
        light: Boolean
    ) {

        // 主色，也就是标题栏的颜色，
        val primaryColor: Int

        // 活跃的颜色，也就是各种按钮控件激活状态的颜色，
        val accentColor: Int

        // 亮色主题，如果是，标题栏状态栏文字图标就要深色，
        val isLight: Boolean
        val tabColorState: ColorStateList
            get() {
                val states = arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                )
                val colors = intArrayOf(
                    normalColor,
                    accentColor
                )
                return ColorStateList(states, colors)
            }
        val mainTabColorState: ColorStateList
            get() {
                val states = arrayOf(
                    intArrayOf(-android.R.attr.state_checked),
                    intArrayOf(android.R.attr.state_checked)
                )
                val colors = intArrayOf(
                    -0xcccccd,
                    accentColor
                )
                return ColorStateList(states, colors)
            }
        val buttonColorState: ColorStateList
            get() {
                val states = arrayOf(
                    intArrayOf(-android.R.attr.state_enabled),
                    intArrayOf(android.R.attr.state_pressed),
                    intArrayOf(android.R.attr.state_focused),
                    intArrayOf()
                )
                val colors = intArrayOf(
                    -0x585859,
                    colorBlend(accentColor, 0x000000, 0.2f),
                    accentColor,
                    accentColor
                )
                return ColorStateList(states, colors)
            }

        /**
         * 用于那些选中时背景色变成皮肤色，未选中时背景镂空，文字色为皮肤色，的按钮，
         */
        val highlightColorState: ColorStateList
            get() {
                val states = arrayOf(intArrayOf(android.R.attr.state_checked), intArrayOf())
                val colors = intArrayOf(
                    -0x1,
                    accentColor
                )
                return ColorStateList(states, colors)
            }

        /**
         * copy from java.awt.Color.brighter,
         */
        val brighterPrimaryColor: Int
            get() {
                val color = primaryColor
                var var1 = Color.red(color)
                var var2 = Color.green(color)
                var var3 = Color.blue(color)
                val var4 = Color.alpha(color)
                val var5: Byte = 3
                return if (var1 == 0 && var2 == 0 && var3 == 0) {
                    Color.argb(var4, var5.toInt(), var5.toInt(), var5.toInt())
                } else {
                    if (var1 in 1 until var5) {
                        var1 = var5.toInt()
                    }
                    if (var2 in 1 until var5) {
                        var2 = var5.toInt()
                    }
                    if (var3 in 1 until var5) {
                        var3 = var5.toInt()
                    }
                    Color.argb(
                        var4,
                        (var1.toDouble() / 0.7).toInt().coerceAtMost(255),
                        (var2.toDouble() / 0.7).toInt().coerceAtMost(255),
                        (var3.toDouble() / 0.7).toInt().coerceAtMost(255)
                    )
                }
            }

        /**
         * colorName是资源id，不保证每次编译都相同，不能参与计算，
         * 其他字段都要参与，
         */
        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false
            val skin = o as Skin
            return primaryColor == skin.primaryColor && accentColor == skin.accentColor && isLight == skin.isLight
        }

        /**
         * colorName是资源id，不保证每次编译都相同，不能参与计算，
         * 其他字段都要参与，
         */
        override fun hashCode(): Int {
            return Objects.hash(primaryColor, accentColor, isLight)
        }

        companion object {
            // 表示未激活的灰色，
            private const val normalColor = -0xcccccd

            /**
             * 颜色叠加算法，
             *
             * @param color        叠加前的颜色RGB，
             * @param overlayColor 要叠加的颜色RGB，
             * @param alpha        叠加颜色的透明度，0-1，1是不透明，
             */
            fun colorBlend(color: Int, overlayColor: Int, alpha: Float): Int {
                val r = Color.red(color)
                val g = Color.green(color)
                val b = Color.blue(color)
                val ovR = Color.red(overlayColor)
                val ovG = Color.green(overlayColor)
                val ovB = Color.blue(overlayColor)
                val newR = singleColorBlend(r, ovR, alpha)
                val newG = singleColorBlend(g, ovG, alpha)
                val newB = singleColorBlend(b, ovB, alpha)
                return Color.rgb(newR, newG, newB)
            }

            /**
             * 计算红绿蓝其一叠加后的色值，
             */
            fun singleColorBlend(color: Int, overlay: Int, alpha: Float): Int {
                return (color * (1 - alpha) + overlay * alpha).toInt()
            }
        }

        init {
            this.primaryColor = -0x1000000 or primaryColor
            this.accentColor = -0x1000000 or accentColor
            isLight = light
        }
    }
}