package com.vpn.utils

import com.tencent.mmkv.MMKV
import com.vpn.base.VpnApp

object Stander {
    @JvmStatic
    fun mmkv():MMKV{
        return try {
           MMKV.defaultMMKV()
        }catch (e:Exception){
            MMKV.initialize(VpnApp.app)
            MMKV.defaultMMKV()
        }

    }
}