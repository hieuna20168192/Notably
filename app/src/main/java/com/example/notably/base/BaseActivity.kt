package com.example.notably.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel

abstract class BaseActivity<V : ViewModel, B : ViewDataBinding> :
    AppCompatActivity() {

    @get: LayoutRes
    protected abstract val layoutId: Int

    protected abstract val viewModel: V

    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.lifecycleOwner = this

        initComponents()
        initListeners()
    }

    abstract fun initComponents()
    abstract fun initListeners()
}
