package com.vpn.ui.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.RemoteException
import android.util.Log
import androidx.lifecycle.*
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.vpn.base.SingleLiveEvent
import com.vpn.base.VpnApp
import com.vpn.bean.Server
import com.vpn.utils.KVUtils
import com.vpn.utils.Utils
import de.blinkt.openvpn.OpenVpnApi
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.OpenVPNThread
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception

data class Data(
    var duration: String,
    var lastPacketReceive: String,
    var byteIn: String,
    var byteOut: String
)

class ServiceUtils(var act: Activity) : DefaultLifecycleObserver {

    var vpnStart = false
    private var lifecycleOwner: LifecycleOwner? = null
    var state: MutableLiveData<String?> = SingleLiveEvent()
    var toast: MutableLiveData<String> = SingleLiveEvent()
    var data: MutableLiveData<Data> = SingleLiveEvent()
    var serverData: MutableLiveData<String?> = SingleLiveEvent()

    private val internetStatus: Boolean
        get() = Utils.netCheck(act)

    private val isServiceRunning: Unit
        get() {
            setStatus(OpenVPNService.getStatus())
        }

    fun attachLifecycleOwner(owner: LifecycleOwner?) {
        lifecycleOwner = owner
        lifecycleOwner?.lifecycle?.addObserver(this)
    }


    private var server: Server? = null
    fun prepareVpn() {
        if (!vpnStart) {
            if (internetStatus) {
                // Checking permission for network monitor
                val intent = VpnService.prepare(act)
                if (intent != null) {
                    act.startActivityForResult(intent, 1)
                } else {
                    startVpn()
                }
                state.postValue("connecting")
            } else {
                toast.postValue("you have no internet connection !!")

            }
        } else if (stopVpn()) {
            toast.postValue("Disconnect Successfully")
        }
    }

    fun stopVpn(): Boolean {
        try {
            OpenVPNThread.stop()
            state.postValue("connect")
            vpnStart = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun startVpn() {
        try {
            if (server?.ovpn == null) {
                Log.i("startVPN", "startVpn()  error")
                return
            }
            server?.ovpn?.apply {
                val conf = act.assets.open(this)
                val isr = InputStreamReader(conf)
                val br = BufferedReader(isr)
                var config = ""
                var line: String?
                while (true) {
                    line = br.readLine()
                    if (line == null) break
                    config += """$line""".trimIndent()
                }
                br.readLine()
                server?.let {
                    OpenVpnApi.startVpn(
                        act,
                        config,
                        it.country,
                        it.ovpnUserName,
                        it.ovpnUserPassword
                    )
                }
                state.postValue("Connecting...")
                vpnStart = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }

    fun setStatus(connectionState: String?) {
        if (connectionState != null) {
            when (connectionState) {
                "DISCONNECTED" -> {
                    vpnStart = false
                }
                "CONNECTED" -> {
                    vpnStart = true // it will use after restart this activity
                }
                "RECONNECTING" -> {
                }
            }
        }
        state.postValue(connectionState)
    }


    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                setStatus(intent.getStringExtra("state"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                var duration = intent.getStringExtra("duration")
                var lastPacketReceive = intent.getStringExtra("lastPacketReceive")
                var byteIn = intent.getStringExtra("byteIn")
                var byteOut = intent.getStringExtra("byteOut")
                if (duration == null) duration = "00:00:00"
                if (lastPacketReceive == null) lastPacketReceive = "0"
                if (byteIn == null) byteIn = " "
                if (byteOut == null) byteOut = " "
                data.postValue(Data(duration, lastPacketReceive, byteIn, byteOut))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        LocalBroadcastManager.getInstance(act).unregisterReceiver(broadcastReceiver)
        super.onPause(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        LocalBroadcastManager.getInstance(act)
            .registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        VpnApp.serveData.observe(owner, Observer {
            if (it != null) {
                newServer(it)
            }
            VpnApp.serveData.value = null

        })
        super.onResume(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        server?.apply {
            KVUtils.saveServer(this)
        }
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        lifecycleOwner?.lifecycle?.removeObserver(this)
        super.onDestroy(owner)
    }

    private fun newServer(server: Server?) {
        this.server = server
        server?.flagUrl?.apply {
            serverData.postValue(this)
        }
        if (vpnStart) {
            stopVpn()
        }
        prepareVpn()
    }
}