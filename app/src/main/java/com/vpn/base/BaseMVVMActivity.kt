package com.vpn.base

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.vpn.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext


abstract class BaseMVVMActivity<V : ViewBinding, VM : BaseViewModel> : SetActionBarActivity(), CoroutineScope,
    HasDefaultViewModelProviderFactory {
    private lateinit var job: Job
    protected lateinit var binding: V
    protected lateinit var viewModel: VM
    protected val loading: Dialog by lazy { createDialog() }

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreate(savedInstanceState: Bundle?) {
        job = Job()
        super.onCreate(savedInstanceState)
        setContentView(createContentView())
        onViewCreate(savedInstanceState)
    }

    abstract fun onViewCreate(savedInstanceState: Bundle?)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        lifecycle.removeObserver(viewModel)
    }

    private fun createContentView(): View {
        binding = createViewBinding()
        // 处理vm相关
        initViewModel()
        return binding.root
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(createViewModel())
        lifecycle.addObserver(viewModel)
        viewModel.loading().observe(this) {
            if (it) {
                showLoading()
            } else {
                dismissLoading()
            }
        }
    }

    protected abstract fun createViewBinding(): V

    protected abstract fun createViewModel(): Class<VM>

    private fun createDialog(): Dialog {
        val dialog = ProgressDialog(this, R.style.transparent_dialog)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun showLoading() {
        if (!loading.isShowing) {
            loading.show()
        }
    }

    private fun dismissLoading() {
        if (loading.isShowing) {
            loading.dismiss()
        }
    }

    /**
     * 配置默认的构造工厂
     * @return
     */
    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }
}