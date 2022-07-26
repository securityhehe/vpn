package com.vpn.bean

data class Server(
    var country: String? = null,
    var flagUrl: String? = null,
    var ovpn: String? = null,
    var ovpnUserName: String? = null,
    var ovpnUserPassword: String? = null,
    var isSelect:Boolean= false,
)