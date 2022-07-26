package com.vpn.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding


abstract class BaseMVVMFragment<T : ViewBinding, VM : BaseViewModel> : Fragment(), HasDefaultViewModelProviderFactory {

    private var _binding: T? = null

    protected val binding get() = _binding!!

    protected lateinit var viewModel: VM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = createViewBinding(inflater, container)
        // 处理vm相关
        initViewModel()
        return binding.root
    }

    abstract fun createViewModel(): Class<VM>

    abstract fun createViewBinding(inflater: LayoutInflater, container: ViewGroup?): T

    private fun initViewModel() {
        viewModel = ViewModelProvider(this).get(createViewModel())
        lifecycle.addObserver(viewModel)
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}