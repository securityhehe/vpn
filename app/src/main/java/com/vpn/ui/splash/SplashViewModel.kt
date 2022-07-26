package com.vpn.ui.splash

import android.app.Application
import androidx.lifecycle.LiveData
import com.vpn.base.BaseViewModel
import com.vpn.base.SingleLiveEvent
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map


class SplashViewModel(mApplication: Application) : BaseViewModel(mApplication) {
    private val systemData by lazy { SingleLiveEvent<SystemBean>() }

    fun getSystemData(): LiveData<SystemBean> {
        return systemData
    }

    override var coroutineExceptionHandler = CoroutineExceptionHandler { _, _ ->
        systemData.postValue(null)
        dismissLoading()
    }

    fun requestSystemData() {
        SystemRepository.requestSystemData().map{ user ->
            systemData.postValue(user)
        }.launchIn(scope)
    }
}
