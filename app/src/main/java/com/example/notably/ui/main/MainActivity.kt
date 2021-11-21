package com.example.notably.ui.main

import androidx.activity.viewModels
import com.example.notably.R
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutId = R.layout.activity_main

    override val viewModel: MainViewModel by viewModels()

    override fun initComponents() {

    }

    override fun initListeners() {

    }
}
