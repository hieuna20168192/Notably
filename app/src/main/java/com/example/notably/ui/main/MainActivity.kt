package com.example.notably.ui.main

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.navigation.NavController
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

    lateinit var moreOptions: ImageView
    lateinit var extraAction: Button
    lateinit var toolbarTitle: TextView

    lateinit var toolbarSelector: RelativeLayout
    lateinit var toolbarSelectorClose: ImageView
    lateinit var toolbarSelectorDeleteNotes: ImageView
    lateinit var toolbarSelectorSelectedItems: TextView

    private lateinit var navController: NavController

    override fun initComponents() {
        navController =
            (supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment).navController
        binding.navigationDrawer.setupWithNavController(navController)

        moreOptions = binding.moreOptions
        toolbarTitle = binding.toolbarTitle
        toolbarSelectorClose = binding.includeToolbarSelector.goBack
        toolbarSelector = binding.includeToolbarSelector.toolbarSelector
        toolbarSelectorDeleteNotes = binding.includeToolbarSelector.deleteNote
        toolbarSelectorSelectedItems = binding.includeToolbarSelector.selectedItems
        extraAction = binding.extraAction
    }

    override fun initListeners() {
        initMenuNavListener()
    }

    private fun initMenuNavListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.moreOptions.isGone = destination.id != R.id.home_dest
            binding.extraAction.isGone = destination.id != R.id.trash_dest
            binding.extraAction.text = getString(R.string.delete_all)
        }
    }
}
