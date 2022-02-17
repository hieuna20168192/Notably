package com.example.notably.repos.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Int = 0,

    @ColumnInfo(name = "task_title")
    val title: String = "",

    @ColumnInfo(name = "task_details")
    val details: String = "",

    @ColumnInfo(name = "task_created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "task_state")
    var state: Boolean = false,

    @ColumnInfo(name = "task_priority")
    var priority: Boolean = false,

    @ColumnInfo(name = "task_list")
    val todoListId: Int = -1
) : Serializable {
    override fun toString(): String {
        return "$title : $createdAt"
    }
}

object TaskDiff : DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.taskId == newItem.taskId
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}
