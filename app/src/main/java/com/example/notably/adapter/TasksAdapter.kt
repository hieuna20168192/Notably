package com.example.notably.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.LayoutItemTodoBinding
import com.example.notably.repos.entities.Task
import com.example.notably.repos.entities.TaskDiff

class TasksAdapter(
    private val taskListener: TaskListener
) : ListAdapter<Task, TasksAdapter.TasksViewHolder>(TaskDiff) {

    interface TaskListener {
        fun onTaskClicked(task: Task, position: Int)
        fun onTaskLongClicked(task: Task, position: Int): Boolean
        fun onTaskStateClicked(task: Task, position: Int, checked: Boolean)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TasksViewHolder {
        return TasksViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TasksViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(taskListener, task, position)
    }

    class TasksViewHolder private constructor(private val binding: LayoutItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: TaskListener,
            task: Task,
            position: Int
        ) {
            binding.run {
                this.listener = listener
                this.task = task
                this.pos = position
                itemTodoCheckbox.setOnCheckedChangeListener { _, isChecked ->
                    listener.onTaskStateClicked(task, position, isChecked)
                }
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): TasksViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemTodoBinding.inflate(layoutInflater, parent, false)
                return TasksViewHolder(binding)
            }
        }
    }
}
