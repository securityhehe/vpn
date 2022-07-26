package com.vpn.ui.service

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.vpn.R
import com.vpn.adapter.ServerListRVAdapter
import com.vpn.base.BaseMVVMActivity
import com.vpn.bean.Server
import com.vpn.databinding.ConfigDialogBinding
import com.vpn.databinding.LogoutDialogBinding
import com.vpn.databinding.ServerSelectActivityBinding
import com.vpn.ui.home.IChangeServer
import com.vpn.utils.Utils

class SelectServerActivity : BaseMVVMActivity<ServerSelectActivityBinding, SelectServiceVM>() ,IChangeServer {

    lateinit var adapter: ServerListRVAdapter
    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreate(savedInstanceState: Bundle?) {
        adapter = ServerListRVAdapter(this)
        supportActionBar?.hide()
        binding.rv.layoutManager = LinearLayoutManager(this)
        binding.rv.adapter = adapter
        viewModel.getSystemData().observe(this){
            adapter.serverLists.clear()
            adapter.serverLists.addAll(it)
            adapter.notifyDataSetChanged()
        }
        viewModel.getServerList()
        binding.back.setOnClickListener{
            finish()
        }
    }

    override fun createViewBinding(): ServerSelectActivityBinding {
        return ServerSelectActivityBinding.inflate(LayoutInflater.from(this))
    }

    override fun createViewModel(): Class<SelectServiceVM> {
        return SelectServiceVM::class.java
    }

    //切换服务器
    override fun clickedItem(pos: Int, server: Server) {
        createLogoutDialog(pos,server).show()
    }

    private fun createLogoutDialog(pos: Int,server: Server): Dialog {
        val dialog = Dialog(this)
        val inflate = LayoutInflater.from(this).inflate(R.layout.config_dialog, null)
        val bind = ConfigDialogBinding.bind(inflate)
        val layoutParams = ViewGroup.LayoutParams(Utils.dip2px(this,315f), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setContentView(inflate, layoutParams)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawableResource(R.drawable.exit_dialog_bg)
        bind.logoutCancel.setOnClickListener { dialog.dismiss() }
        bind.logoutBtn.setOnClickListener {
            adapter.changeState(true,pos)
            //调用服务修改链接方法。
            dialog.dismiss()
        }
        return dialog
    }

}
