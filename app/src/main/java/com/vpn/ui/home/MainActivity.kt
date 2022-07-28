package com.vpn.ui.home

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.vpn.base.SetActionBarActivity
import com.vpn.R
import com.vpn.adapter.MenuListRVAdapter
import com.vpn.bean.MenuItem
import com.vpn.databinding.LogoutDialogBinding
import com.vpn.databinding.MainActivityBinding
import com.vpn.ui.abount.AboutActivity
import com.vpn.ui.service.SelectServerActivity
import com.vpn.ui.webview.WebH5Activity
import com.vpn.utils.Constants.ABOUT
import com.vpn.utils.Constants.EXIT
import com.vpn.utils.Constants.FAQ
import com.vpn.utils.Constants.PRIVACY_POLICY
import com.vpn.utils.Constants.SHARE
import com.vpn.utils.Utils

class MainActivity : SetActionBarActivity(), NavItemClickListener {

    private val transaction = supportFragmentManager.beginTransaction()
    private lateinit var fragment: MainFragment
    private var serverUtils = ServiceUtils(this)
    private var menuList: ArrayList<MenuItem> = arrayListOf(
        MenuItem(R.mipmap.main_menu_share, R.string.menu_share, R.mipmap.menu_go, SHARE),
        MenuItem(R.mipmap.main_menu_faq, R.string.menu_faq, R.mipmap.menu_go, FAQ),
        MenuItem( R.mipmap.main_menu_privcy_private, R.string.menu_privacy_policy, R.mipmap.menu_go, PRIVACY_POLICY ),
        MenuItem(R.mipmap.main_menu_info, R.string.menu_about, R.mipmap.menu_go, ABOUT),
        MenuItem(R.mipmap.main_menu_logout, R.string.menu_exit, R.mipmap.menu_go, EXIT)
    )

    private var serverListRVAdapter: MenuListRVAdapter? = null
    private lateinit var mVH: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mVH = MainActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(mVH.root)
        serverUtils.attachLifecycleOwner(this);
        supportActionBar?.hide()
        fragment = MainFragment()
        fragment.ser = serverUtils;
        transaction.add(R.id.container, fragment)
        transaction.commit()
        mVH.content.toolbar.navbarLeft.setOnClickListener { closeDrawer() }
        mVH.content.toolbar.navbarRight.setOnClickListener { startSelectService() }
        serverListRVAdapter = MenuListRVAdapter(menuList, this)
        mVH.serverListRv.layoutManager = LinearLayoutManager(this)
        mVH.serverListRv.adapter = serverListRVAdapter
        mVH.serverListRv.adapter?.notifyDataSetChanged()
    }

    private fun startSelectService() {
        startActivity(Intent(this, SelectServerActivity::class.java))
    }

    private fun closeDrawer() {
        if (mVH.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mVH.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            mVH.drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    override fun clickedItem(opt: Int) {
        closeDrawer()
        when (opt) {
            SHARE -> {
                share()
            }
            FAQ -> {
                val intent = Intent(this, WebH5Activity::class.java)
                intent.putExtra("url", "https://www.fastproxy.live/text/FAQ.html")
                intent.putExtra("title", "FAQ")
                startActivity(intent)
            }
            PRIVACY_POLICY -> {
                val intent = Intent(this@MainActivity,WebH5Activity::class.java)
                intent.putExtra("url", "https://www.fastproxy.live/text/pfp-word-p.html")
                intent.putExtra("title", "Privacy policy")
                startActivity(intent);
            }
            ABOUT -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
            }
            EXIT -> {
                exit()
            }
        }
    }

    companion object {
        val TAG = MainActivity::class.java.name.toString()
    }

    //分享。
    private fun share() {

    }

    //退出弹出
    private fun exit() {
        createLogoutDialog().show()
    }

    private fun createLogoutDialog(): Dialog {
        val dialog = Dialog(this)
        val inflate = LayoutInflater.from(this).inflate(R.layout.logout_dialog, null)
        val bind = LogoutDialogBinding.bind(inflate)
        val layoutParams =
            ViewGroup.LayoutParams(Utils.dip2px(this, 315f), ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog.setContentView(inflate, layoutParams)
        dialog.window?.setBackgroundDrawableResource(R.drawable.exit_dialog_bg)
        dialog.window?.setGravity(Gravity.CENTER)
        bind.logoutCancel.setOnClickListener { dialog.dismiss() }
        bind.logoutBtn.setOnClickListener {
            dialog.dismiss()
            finish()
        }
        return dialog
    }


}