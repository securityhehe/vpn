package com.vpn.ui.service

import android.app.Application
import androidx.lifecycle.LiveData
import com.vpn.R
import com.vpn.base.BaseViewModel
import com.vpn.base.SingleLiveEvent
import com.vpn.bean.Server
import com.vpn.utils.Utils
import kotlinx.coroutines.CoroutineExceptionHandler


class SelectServiceVM(mApplication: Application) : BaseViewModel(mApplication) {
    private val systemData by lazy { SingleLiveEvent<MutableList<Server>>() }

    fun getSystemData(): LiveData<MutableList<Server>> {
        return systemData
    }

    override var coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
        systemData.postValue(null)
        dismissLoading()
    }

    open fun getServerList() {
        val servers = ArrayList<Server>()
        servers.add(
            Server(
                "United States",
                Utils.getImgURL(R.drawable.flag_usa),
                "us.ovpn",
                "freeopenvpn",
                "416248023"
            )
        )
        servers.add(
            Server(
                "Japan",
                Utils.getImgURL(R.drawable.flag_japan),
                "japan.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            Server(
                "Sweden",
                Utils.getImgURL(R.drawable.flag_sweden),
                "sweden.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            Server(
                "Korea",
                Utils.getImgURL(R.drawable.flag_korea),
                "korea.ovpn",
                "vpn",
                "vpn"
            )
        )
        servers.add(
            Server(
                "HK",
                Utils.getImgURL(R.drawable.flag_korea),
                "test.ovpn",
                "vpn",
                "vpn"
            )
        )
        systemData.postValue(servers);
    }
    

}
