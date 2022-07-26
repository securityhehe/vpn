package com.vpn.ui.home

import com.vpn.bean.Server

interface IChangeServer {
    fun clickedItem(opt: Int, server: Server)
}