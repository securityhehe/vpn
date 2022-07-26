package com.vpn.utils

import com.vpn.R
import com.vpn.bean.Server
import com.vpn.utils.Utils.getImgURL
object KVUtils {


    /**
     * Save server details
     * @param server details of ovpn server
     */
    fun saveServer(server: Server) {
        Stander.mmkv().encode(SERVER_COUNTRY, server.country)
        Stander.mmkv().encode(SERVER_FLAG, server.flagUrl)
        Stander.mmkv().encode(SERVER_OVPN, server.ovpn)
        Stander.mmkv().encode(SERVER_OVPN_USER, server.ovpnUserName)
        Stander.mmkv().encode(SERVER_OVPN_PASSWORD, server.ovpnUserPassword)
    }

    /**
     * Get server data from shared preference
     * @return server model object
     */
    val server: Server
        get() = Server(
            Stander.mmkv().decodeString(SERVER_COUNTRY, "Japan"),
            Stander.mmkv().decodeString(SERVER_FLAG, getImgURL(R.drawable.flag_japan)),
            Stander.mmkv().decodeString(SERVER_OVPN, "japan.ovpn"),
            Stander.mmkv().decodeString(SERVER_OVPN_USER, "vpn"),
            Stander.mmkv().decodeString(SERVER_OVPN_PASSWORD, "vpn")
        )

        private const val APP_PREFS_NAME = "CakeVPNPreference"
        private const val SERVER_COUNTRY = "server_country"
        private const val SERVER_FLAG = "server_flag"
        private const val SERVER_OVPN = "server_ovpn"
        private const val SERVER_OVPN_USER = "server_ovpn_user"
        private const val SERVER_OVPN_PASSWORD = "server_ovpn_password"


}