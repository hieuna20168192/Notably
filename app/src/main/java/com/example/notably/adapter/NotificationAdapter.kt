package com.example.notably.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.LayoutItemNotificationBinding
import com.example.notably.repos.entities.Notification
import com.example.notably.repos.entities.NotificationDiff

class NotificationAdapter(
    private val notificationListener: NotificationListener
) : ListAdapter<Notification, NotificationAdapter.NotificationViewHolder>(NotificationDiff) {

    interface NotificationListener {
        fun onNotificationClicked(notification: Notification, pos: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = getItem(position)
        holder.bind(notificationListener, notification, position)
    }

    class NotificationViewHolder private constructor(private val binding: LayoutItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notificationListener: NotificationListener, notification: Notification, pos: Int) {
            binding.run {
                this.listener = notificationListener
                this.notification = notification
                this.pos = pos
            }
        }

        companion object {
            fun from(parent: ViewGroup): NotificationViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemNotificationBinding.inflate(layoutInflater, parent, false)
                return NotificationViewHolder(binding)
            }
        }
    }
}
