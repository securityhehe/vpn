package com.vpn.ui.private

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.lifecycle.lifecycleScope
import com.vpn.base.BaseMVVMActivity
import com.vpn.databinding.SplashActivityBinding
import com.vpn.ui.home.MainActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

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
        viewModel.getSystemData().observe(this@SplashActivity) {
            launch {
                withContext(Dispatchers.Default) {
                    delay(2000)
                }
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }
        var i = 1
        lifecycleScope.launchWhenResumed {
            val tickerChannel = ticker(delayMillis = 10, initialDelayMillis = 0)
            repeat(200) {
                tickerChannel.receive()
                binding.mProgress.progress = (i++)
            }
            tickerChannel.cancel()
        }

    }


}