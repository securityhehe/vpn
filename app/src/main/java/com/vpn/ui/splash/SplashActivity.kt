package com.vpn.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.vpn.base.BaseMVVMActivity
import com.vpn.databinding.SplashActivityBinding
import com.vpn.ui.home.MainActivity
import com.vpn.ui.private.PrivateActivity
import com.vpn.utils.KVUtils
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseMVVMActivity<SplashActivityBinding, SplashViewModel>() {

    override fun createViewBinding(): SplashActivityBinding {
        return SplashActivityBinding.inflate(layoutInflater)
    }

    override fun createViewModel(): Class<SplashViewModel> {
        return SplashViewModel::class.java
    }

    override fun onViewCreate(savedInstanceState: Bundle?) {
        viewModel.requestSystemData()
        val tickerChannel = ticker(delayMillis = 10, initialDelayMillis = 0)
        var i = 1
        lifecycleScope.launchWhenResumed {
            launch {
                repeat(200) {
                    tickerChannel.receive()
                    binding.mProgress.progress = (i++)
                }
                tickerChannel.cancel()
            }
            viewModel.getSystemData().observe(this@SplashActivity) {
                launch {
                    repeat(100) {
                        tickerChannel.receive()
                        i += 2
                        if (i >= 200) {
                            i = 200
                        }
                        binding.mProgress.progress = i
                    }
                    tickerChannel.cancel()
                    if(KVUtils.isReadPrivate()) {
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                        finish()
                    }else{
                        startActivity(Intent(this@SplashActivity, PrivateActivity::class.java))
                        finish()
                    }

                }
            }
        }

    }


}