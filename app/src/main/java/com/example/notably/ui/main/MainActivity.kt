package com.example.notably.ui.main

import android.content.Intent
import android.util.Log
import android.widget.*
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.notably.R
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityMainBinding
import com.example.notably.repos.entities.Notification
import com.example.notably.ui.home.HomeFragmentDirections
import com.example.notably.ui.notification.NotificationsFragmentDirections
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint

const val TOPIC = "note"

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
    lateinit var deleteAllNotifications: ImageView

    private lateinit var navController: NavController

    private var notification: Notification? = null

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
        deleteAllNotifications = binding.deleteAllNotifications
        extraAction = binding.extraAction
        subscribeTopic()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.d("MainActivity", "onNewIntent")
        initNotification(intent)
        navNotificationScreen()
    }

    private fun navNotificationScreen() {
        val notificationDirection = NotificationsFragmentDirections.globalActionNotification()
        navController.navigate(notificationDirection)
    }

    private fun initNotification(intent: Intent) {
        notification = intent.getSerializableExtra("notification") as? Notification
        notification?.let {
            viewModel.saveNotification(it)
        }
    }

    private fun subscribeTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic(TOPIC)
            .addOnCompleteListener { task ->
                var msg = getString(R.string.message_subscribed)
                if (!task.isSuccessful) {
                    msg = getString(R.string.message_subscribe_failed)
                }
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                Log.d("subscribeTopic", msg)
            }
    }

    override fun initListeners() {
        initMenuNavListener()
    }

    private fun initMenuNavListener() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.moreOptions.isGone = destination.id != R.id.home_dest
            binding.extraAction.isGone = destination.id != R.id.trash_dest
            binding.extraAction.text = getString(R.string.delete_all)
            binding.deleteAllNotifications.isGone = destination.id != R.id.notification_dest
        }
    }
}
