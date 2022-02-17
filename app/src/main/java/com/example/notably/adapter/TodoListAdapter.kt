package com.example.notably.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.LayoutItemTodoListBinding
import com.example.notably.repos.entities.TodoListDiff
import com.example.notably.repos.entities.TodosList

class TodoListAdapter(
    private val todoListListener: TodoListListener
) : ListAdapter<TodosList, TodoListAdapter.TodoListViewHolder>(TodoListDiff) {

    interface TodoListListener {
        fun onTodoListClicked(todosList: TodosList, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoListViewHolder {
        return TodoListViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TodoListViewHolder, position: Int) {
        val todoList = getItem(position)
        holder.bind(todoListListener, todoList, position)
    }

    class TodoListViewHolder private constructor(private val binding: LayoutItemTodoListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(listener: TodoListListener, todoList: TodosList, position: Int) {
            binding.run {
                this.listener = listener
                this.todoList = todoList
                this.pos = position
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): TodoListViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemTodoListBinding.inflate(layoutInflater, parent, false)
                return TodoListViewHolder(binding)
            }
        }
    }
}
