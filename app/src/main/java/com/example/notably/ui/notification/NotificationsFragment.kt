package com.example.notably.ui.notification

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.NotificationAdapter
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentNotificationsBinding
import com.example.notably.extensions.Helper
import com.example.notably.repos.entities.Notification
import com.example.notably.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NotificationsFragment : BaseFragment<NotificationViewModel, FragmentNotificationsBinding>(),
    NotificationAdapter.NotificationListener {

    override val layoutId: Int = R.layout.fragment_notifications

    override val viewModel: NotificationViewModel by viewModels()

    private val notificationsAdapter: NotificationAdapter = NotificationAdapter(this)

    override fun initComponents() {
        initNotificationsRcl()
        initNotificationsObserver()
        initNotifications()
    }

    private fun initNotificationsObserver() {
        viewModel.notifications.observe(this) {
            notificationsAdapter.submitList(it)
            binding.noItems.isGone = it.isNotEmpty()
            (requireActivity() as MainActivity).deleteAllNotifications.isGone = it.isEmpty()
        }
    }

    private fun initNotifications() {
        viewModel.getNotifications()
    }

    private fun initNotificationsRcl() {
        binding.notificationsList.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = notificationsAdapter
        }
    }

    override fun initListeners() {
        initDeleteAllNotificationsListener()
    }

    private fun initDeleteAllNotificationsListener() {
        (requireActivity() as MainActivity).deleteAllNotifications.setOnClickListener {
            viewModel.deleteAllNotifications()
        }
    }

    override fun onNotificationClicked(notification: Notification, pos: Int) {
        requestPreviewNotification(notification.copy())
    }

    private fun requestPreviewNotification(notification: Notification) {
        val dialog = createPreviewDialog()
        val title = dialog.findViewById<TextView>(R.id.notification_title)
        val description = dialog.findViewById<TextView>(R.id.notification_description)
        val confirmAllow = dialog.findViewById<TextView>(R.id.confirm_allow)
        val date = dialog.findViewById<TextView>(R.id.notification_date)

        title.text = notification.title
        description.text = notification.content
        date.text = Helper.get_formatted_date(notification.createdAt)
        confirmAllow.setOnClickListener {
            notification.isRead = true
            viewModel.saveNotification(notification)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun createPreviewDialog(): Dialog {
        val dialog = Dialog(requireContext())
        dialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.preview_notification_dialog)
            setCancelable(true)
            setOnCancelListener { dismiss() }
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT)
            }
        }
        return dialog
    }
}
