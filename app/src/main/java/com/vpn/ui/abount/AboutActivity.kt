package com.vpn.ui.abount

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.vpn.BuildConfig
import com.vpn.R
import com.vpn.base.BaseMVVMActivity
import com.vpn.base.SetActionBarActivity
import com.vpn.databinding.AboutActivityBinding
import com.vpn.databinding.PrivateActivityBinding
import com.vpn.ui.home.MainActivity
import com.vpn.ui.webview.WebH5Activity
import com.vpn.utils.KVUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ticker

@SuppressLint("CustomSplashScreen")
class AboutActivity : SetActionBarActivity() {

    private lateinit var vh:AboutActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vh = AboutActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(vh.root)
        vh.version.text = String.format(getString(R.string.app_version),BuildConfig.VERSION_NAME)
    }
}