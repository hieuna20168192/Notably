package com.example.notably.repos.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "todo_lists")
data class TodosList(
    @PrimaryKey(autoGenerate = true)
    val todoListId: Int = 0,
    @ColumnInfo(name = "todo_list_identifier")
    val identifier: Int = 0,
    @ColumnInfo(name = "todo_list_title")
    val title: String = ""
) : Serializable {

    override fun toString(): String {
        return title
    }
}

object TodoListDiff : DiffUtil.ItemCallback<TodosList>() {
    override fun areItemsTheSame(oldItem: TodosList, newItem: TodosList): Boolean {
        return oldItem.todoListId == newItem.todoListId
    }

    override fun areContentsTheSame(oldItem: TodosList, newItem: TodosList): Boolean {
        return oldItem == newItem
    }
}
