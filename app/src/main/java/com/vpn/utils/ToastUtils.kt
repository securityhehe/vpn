package com.vpn.utils

import android.widget.Toast
import com.vpn.base.VpnApp


object ToastUtils {
    fun toast(msg: String?) {
        Toast.makeText(VpnApp.app, msg, Toast.LENGTH_SHORT).show()
    }

    fun toast(msgId: Int) {
        Toast.makeText(VpnApp.app, msgId, Toast.LENGTH_SHORT).show()
    }
}