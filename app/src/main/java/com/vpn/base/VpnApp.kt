package com.vpn.base

import android.app.Application
import android.util.Log
import com.vpn.BuildConfig
import com.vpn.bean.Server
import com.zdu.kt.KtHttpClient
import com.zdu.lib.HttpClient
import com.zdu.lib.IConfigValue
import com.zdu.lib.log.HttpLog

class VpnApp : Application() {

    companion object{
        var app: VpnApp? = null
        val serveData = SingleLiveEvent<Server?>()
    }

    override fun onCreate() {
        super.onCreate()
        app = this
        val build = HttpClient.Builder().setDebug(BuildConfig.DEBUG).connectionTimeOutSecond(15).writeTimeOutSecond(15).readTimeOutSecond(15)
                .configValue(object : IConfigValue.Header {
                    override fun token(): String {
                        return "" ;
                    }

                    override fun language(): String {
                        return "zn"
                    }

                }).log(object : HttpLog.ILog {
                    override fun d(p0: String) {
                        Log.d("zdu__", p0)
                    }

                    override fun e(p0: String) {
                        Log.e("zdu__", p0)
                    }

                }).build()
        KtHttpClient.init(build)
    }

}