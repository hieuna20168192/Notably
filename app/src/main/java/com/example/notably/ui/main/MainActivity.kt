package com.example.notably.ui.main

import androidx.activity.viewModels
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavController
import androidx.navigation.NavHost
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.notably.R
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val layoutId = R.layout.activity_main

    override val viewModel: MainViewModel by viewModels()

    private lateinit var navController: NavController

    override fun initComponents() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
        binding.navigationDrawer.setupWithNavController(navController)
    }

    override fun initListeners() {

    }
}
