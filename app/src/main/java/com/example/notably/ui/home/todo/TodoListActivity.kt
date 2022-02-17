package com.example.notably.ui.home.todo

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.TasksAdapter
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityTodoListBinding
import com.example.notably.repos.entities.Task
import com.example.notably.repos.entities.TodosList
import com.example.notably.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoListActivity : BaseActivity<HomeViewModel, ActivityTodoListBinding>(),
    TasksAdapter.TaskListener {

    override val layoutId: Int = R.layout.activity_todo_list

    override val viewModel: HomeViewModel by viewModels()

    private lateinit var todosList: TodosList

    private val tasksAdapter: TasksAdapter = TasksAdapter(this)

    private var isUpdate = false

    override fun initComponents() {
        initPrevTodosList()
        bindPrevTodos()
        initTasksRcl()
        initTasksByListObserver()
        initTasksByList()
    }

    private fun initTasksByListObserver() {
        viewModel.tasks.observe(this) {
            tasksAdapter.submitList(it)
            binding.noItems.isGone = it.isNotEmpty()
        }
    }

    private fun initTasksByList() {
        viewModel.getTasksByList(todosList.identifier)
    }

    private fun initTasksRcl() {
        binding.todosRecyclerview.layoutManager =
            LinearLayoutManager(applicationContext, LinearLayoutManager.VERTICAL, false)
        binding.todosRecyclerview.adapter = tasksAdapter
    }

    private fun bindPrevTodos() {
        binding.todoListTitle.text = todosList.title
    }

    private fun initPrevTodosList() {
        if (hasPrevTodosList()) {
            todosList = intent.getSerializableExtra("todo_list") as TodosList
            Log.d("initPrevTodosList", todosList.identifier.toString())
        } else {
            Toast.makeText(this, getString(R.string.error_empty_list), Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasPrevTodosList(): Boolean {
        return intent.getSerializableExtra("todo_list") != null
    }

    override fun initListeners() {
        initGoBackListener()
    }

    private fun initGoBackListener() {
        binding.goBack.setOnClickListener {
            setResultAndFinish()
        }
    }

    private fun setResultAndFinish() {
        val intent = Intent().apply {
            putExtra("is_updated", isUpdate)
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        setResultAndFinish()
    }

    override fun onTaskClicked(task: Task, position: Int) {
        val intent = Intent(this, ViewTodoActivity::class.java)
        intent.putExtra("modifier", true)
        intent.putExtra("todo", task)
        startActivityForResult(intent, REQUEST_VIEW_TODO_CODE)
    }

    override fun onTaskLongClicked(task: Task, position: Int): Boolean {
        return false
    }

    override fun onTaskStateClicked(task: Task, position: Int, checked: Boolean) {
        task.state = checked
        viewModel.saveAndRefreshByList(task, todosList.identifier)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestViewSuccess(requestCode, resultCode)) {
            Log.d("onActivityResult", todosList.identifier.toString())
            when {
                isUpdateAction(data) -> {
                    viewModel.getTasksByList(todosList.identifier)
                    isUpdate = true
                }
                isDeleteAction(data) -> {
                    viewModel.getTasksByList(todosList.identifier)
                    isUpdate = true
                }
                isMarkAction(data) -> {
                    viewModel.getTasksByList(todosList.identifier)
                    isUpdate = true
                }
            }
        }
    }

    private fun isMarkAction(data: Intent?): Boolean {
        return data != null && data.getIntExtra("requestCode", 0) == 2
    }

    private fun requestViewSuccess(requestCode: Int, resultCode: Int): Boolean {
        return requestCode == REQUEST_VIEW_TODO_CODE && resultCode == Activity.RESULT_OK
    }

    private fun isDeleteAction(data: Intent?): Boolean {
        return data != null && data.getIntExtra("requestCode", 0) == 1
    }

    private fun isUpdateAction(data: Intent?): Boolean {
        return data != null && data.getIntExtra("requestCode", 0) == 3
    }

    companion object {
        const val REQUEST_VIEW_TODO_CODE = 2
    }
}
