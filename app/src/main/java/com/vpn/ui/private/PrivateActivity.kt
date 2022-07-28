package com.vpn.ui.private

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.vpn.base.BaseMVVMActivity
import com.vpn.databinding.PrivateActivityBinding
import com.vpn.ui.home.MainActivity
import com.vpn.ui.webview.WebH5Activity
import com.vpn.utils.KVUtils

@SuppressLint("CustomSplashScreen")
class PrivateActivity : BaseMVVMActivity<PrivateActivityBinding, PrivateViewModel>() {

    override fun createViewBinding(): PrivateActivityBinding {
        return PrivateActivityBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): Class<PrivateViewModel> {
        return PrivateViewModel::class.java
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        binding.privateBtn.setOnClickListener{
            val intent = Intent(this@PrivateActivity,WebH5Activity::class.java)
            intent.putExtra("url", "https://www.fastproxy.live/text/pfp-word-p.html")
            intent.putExtra("title", "Privacy policy")
            startActivity(intent);
        }
        binding.btn.setOnClickListener{
            KVUtils.savePrivate()
            startActivity(Intent(this@PrivateActivity,MainActivity::class.java))
            finish()
        }
    }


}