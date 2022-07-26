package com.vpn.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.viewModelScope
import com.vpn.utils.ToastUtils
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.plus


open class BaseViewModel(protected val mApplication: Application) : AndroidViewModel(mApplication), DefaultLifecycleObserver {

    open var coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        ToastUtils.toast(throwable.message)
        dismissLoading()
    }

    protected val scope: CoroutineScope
        get() = viewModelScope + coroutineExceptionHandler

    private val loading: SingleLiveEvent<Boolean> by lazy { SingleLiveEvent() }

    fun loading(): SingleLiveEvent<Boolean> {
        return loading
    }

    fun showLoading() {
        loading.value = true
    }

    fun dismissLoading() {
        loading.value = false
    }
}