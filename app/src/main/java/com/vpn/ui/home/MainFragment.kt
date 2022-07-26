package com.vpn.ui.home

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import android.os.RemoteException
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import de.blinkt.openvpn.core.OpenVPNThread
import de.blinkt.openvpn.core.OpenVPNService
import de.blinkt.openvpn.core.VpnStatus
import de.blinkt.openvpn.OpenVpnApi
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.vpn.R
import com.vpn.bean.Server
import com.vpn.databinding.MainFragmentBinding
import com.vpn.utils.KVUtils
import com.vpn.utils.Utils
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.Exception

class MainFragment : Fragment(), View.OnClickListener, ChangeServer {
    var vpnStart = false
    private var server: Server? = null
    private lateinit var binding:MainFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(inflater);
        initializeAll()
        return binding.root;
    }

    private fun initializeAll() {
         updateCurrentServerIcon(KVUtils.server.flagUrl)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vpnBtn.setOnClickListener(this)
        VpnStatus.initLogCache(activity?.cacheDir)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.vpnBtn ->                 // Vpn is running, user would like to disconnect current connection.
                if (vpnStart) {
                    confirmDisconnect()
                } else {
                    prepareVpn()
                }
        }
    }

    private fun confirmDisconnect() {
        val requireActivity = requireActivity()
        val builder = AlertDialog.Builder(requireActivity)
        builder.setMessage(requireActivity.getString(R.string.connection_close_confirm))
        builder.setPositiveButton(requireActivity.getString(R.string.yes)) { dialog, id -> stopVpn() }
        builder.setNegativeButton(requireActivity.getString(R.string.no)) { dialog, id -> }
        val dialog = builder.create()
        dialog.show()
    }

    private fun prepareVpn() {
        if (!vpnStart) {
            if (internetStatus) {
                // Checking permission for network monitor
                val intent = VpnService.prepare(context)
                if (intent != null) {
                    startActivityForResult(intent, 1)
                } else{
                    startVpn()
                }

                status("connecting")
            } else {
                showToast("you have no internet connection !!")
            }
        } else if (stopVpn()) {
            showToast("Disconnect Successfully")
        }
    }

    private fun stopVpn(): Boolean {
        try {
            OpenVPNThread.stop()
            status("connect")
            vpnStart = false
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            startVpn()
        } else {
            showToast("Permission Deny !! ")
        }
    }

    private val internetStatus: Boolean
        get() = Utils.netCheck(requireActivity());

    private val isServiceRunning: Unit
        get() {
            setStatus(OpenVPNService.getStatus())
        }

    private fun startVpn() {
        try {
            if(server?.ovpn == null){
               Log.i("startVPN", "startVpn()  error")
               return
            }
            server?.ovpn?.apply{
                val conf = requireActivity().assets.open(this)
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
                OpenVpnApi.startVpn(
                    context,
                    config,
                    server!!.country,
                    server!!.ovpnUserName,
                    server!!.ovpnUserPassword
                )
                binding.logTv.text = "Connecting..."
                vpnStart = true
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: RemoteException) {
            e.printStackTrace()
        }
    }


    fun setStatus(connectionState: String?) {
        if (connectionState != null)
            when (connectionState) {
                "DISCONNECTED" -> {
                    status("connect")
                    vpnStart = false
                    OpenVPNService.setDefaultStatus()
                    binding.logTv.setText("")
                }
                "CONNECTED" -> {
                    vpnStart = true // it will use after restart this activity
                    status("connected")
                    binding.logTv.setText("")
                }
                "WAIT" -> binding.logTv.setText("waiting for server connection!!")
                "AUTH" -> binding.logTv.setText("server authenticating!!")
                "RECONNECTING" -> {
                    status("connecting")
                    binding.logTv.setText("Reconnecting...")
                }
                "NONETWORK" -> binding.logTv.setText("No network connection")
            }
    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    private fun status(status: String) {
        when (status) {
            "connect" -> {
                binding.vpnBtn.text = requireActivity().getString(R.string.connect)
            }
            "connecting" -> {
                binding.vpnBtn.text = requireActivity().getString(R.string.connecting)
            }
            "connected" -> {
                binding.vpnBtn.text = requireActivity().getString(R.string.disconnect)
            }
            "tryDifferentServer" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connected)
                binding.vpnBtn.text = "Try Different\nServer"
            }
            "loading" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button)
                binding.vpnBtn.text = "Loading Server.."
            }
            "invalidDevice" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connected)
                binding.vpnBtn.text = "Invalid Device"
            }
            "authenticationCheck" -> {
                binding.vpnBtn.setBackgroundResource(R.drawable.button_connecting)
                binding.vpnBtn.text = "Authentication \n Checking..."
            }
        }
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
                updateConnectionStatus(duration, lastPacketReceive, byteIn, byteOut)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateConnectionStatus(
        duration: String,
        lastPacketReceive: String,
        byteIn: String,
        byteOut: String
    ) {
        binding.durationTv.setText("Duration: $duration")
        binding.lastPacketReceiveTv.setText("Packet Received: $lastPacketReceive second ago")
        binding.byteInTv.setText("Bytes In: $byteIn")
        binding.byteOutTv.setText("Bytes Out: $byteOut")
    }

    private fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun updateCurrentServerIcon(serverIcon: String?) {
        Glide.with(requireActivity())
            .load(serverIcon)
            .into(binding.selectedServerIcon)
    }

    override fun newServer(server: Server?) {
        this.server = server
        server?.flagUrl?.apply {
            updateCurrentServerIcon(this)
        }
        if (vpnStart) {
            stopVpn()
        }
        prepareVpn()
    }

    override fun onResume() {
        LocalBroadcastManager.getInstance(requireActivity()).registerReceiver(broadcastReceiver, IntentFilter("connectionState"))
        super.onResume()
    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(requireActivity()).unregisterReceiver(broadcastReceiver)
        super.onPause()
    }
    override fun onStop() {
        server?.apply {
            KVUtils.saveServer(this)
        }
        super.onStop()
    }
}