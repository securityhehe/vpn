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
import androidx.lifecycle.Observer
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

class MainFragment : Fragment() {
    var ser: ServiceUtils? = null
    private lateinit var binding: MainFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = MainFragmentBinding.inflate(inflater);
        initializeAll()
        return binding.root
    }

    private fun initializeAll() {
        Glide.with(requireActivity()).load(KVUtils.server.flagUrl).into(binding.icon)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.state.setOnClickListener {
            if (ser?.vpnStart == true) {
                confirmDisconnect()
            } else {
                ser?.prepareVpn()
            }
        }
        VpnStatus.initLogCache(activity?.cacheDir)
        ser?.apply {
            this.state.observe(this@MainFragment.viewLifecycleOwner) {
                this@MainFragment.setStatus(it)
            }
            this.toast.observe(this@MainFragment.viewLifecycleOwner) {
                showToast(it)
            }
        }
    }

    private fun confirmDisconnect() {
        val requireActivity = requireActivity()
        val builder = AlertDialog.Builder(requireActivity)
        builder.setMessage(requireActivity.getString(R.string.connection_close_confirm))
        builder.setPositiveButton(requireActivity.getString(R.string.yes)) { dialog, id -> ser?.stopVpn() }
        builder.setNegativeButton(requireActivity.getString(R.string.no)) { dialog, id -> }
        val dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            ser?.startVpn()
        } else {
            showToast("Permission Deny !! ")
        }
    }

    fun setStatus(connectionState: String?) {
        if (connectionState != null)
            when (connectionState) {
                "DISCONNECTED" -> {
                    status("connect")
                    OpenVPNService.setDefaultStatus()
                    binding.stateText.setText("")
                }
                "CONNECTED" -> {
                    status("connected")
                    binding.stateText.text = ""
                }
                "WAIT" -> binding.stateText.text = "waiting for server connection!!"
                "AUTH" -> binding.stateText.text = "server authenticating!!"
                "RECONNECTING" -> {
                    status("connecting")
                    binding.stateText.text = "Reconnecting..."
                }
                "NONETWORK" -> binding.stateText.text = "No network connection"
            }
    }

    /**
     * Change button background color and text
     * @param status: VPN current status
     */
    private fun status(status: String) {
        when (status) {
            "connect" -> {
                binding.state.setBackgroundResource(R.mipmap.main_connect)
                binding.stateText.text = requireActivity().getString(R.string.connect)
            }
            "connecting" -> {
                binding.state.setBackgroundResource(R.mipmap.main_connect)
                binding.stateText.text = requireActivity().getString(R.string.connecting)
            }
            "connected" -> {
                binding.state.setBackgroundResource(R.mipmap.main_connect)
                binding.stateText.text = requireActivity().getString(R.string.disconnect)
            }
            "tryDifferentServer" -> {
                binding.state.setBackgroundResource(R.drawable.button_connected)
                binding.stateText.text = "Try Different\nServer"
            }
            "loading" -> {
                binding.state.setBackgroundResource(R.drawable.button)
                binding.stateText.text = "Loading Server.."
            }
            "invalidDevice" -> {
                binding.state.setBackgroundResource(R.drawable.button_connected)
                binding.stateText.text = "Invalid Device"
            }
            "authenticationCheck" -> {
                binding.state.setBackgroundResource(R.drawable.button_connecting)
                binding.stateText.text = "Authentication \n Checking..."
            }
        }
    }

    private fun showToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}