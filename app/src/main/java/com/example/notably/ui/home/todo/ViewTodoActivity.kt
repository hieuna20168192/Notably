package com.example.notably.ui.home.todo

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.notably.R
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityViewTodoBinding
import com.example.notably.repos.entities.Task
import com.example.notably.ui.home.HomeViewModel
import com.example.notably.ui.sheets.TodoMoveToBottomSheetModal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ViewTodoActivity : BaseActivity<HomeViewModel, ActivityViewTodoBinding>(),
    TodoMoveToBottomSheetModal.OnMoveListener {

    override val layoutId: Int = R.layout.activity_view_todo

    override val viewModel: HomeViewModel by viewModels()

    private val bundle = Bundle()
    private lateinit var presetTodo: Task

    // do-do list identifier
    private var todoListIdentifier = -1

    override fun initComponents() {
        initPresetTodo()
        bindPresetTask()
    }

    private fun bindPresetTask() {
        binding.todoTitle.setText(presetTodo.title)
        binding.todoDetails.setText(presetTodo.details)
        binding.todoCreatedAt.text = presetTodo.createdAt
        binding.todoPriority.isChecked = presetTodo.priority
        binding.includeTaskActions.markTodo.text =
            if (presetTodo.state) getString(R.string.mark_uncompleted) else getString(R.string.mark_completed)
        todoListIdentifier = presetTodo.todoListId
    }

    private fun initPresetTodo() {
        if (intent.getBooleanExtra("modifier", false)) {
            presetTodo = intent.getSerializableExtra("todo") as Task
            bundle.putSerializable("todo_data", presetTodo)
        }
    }

    override fun initListeners() {
        initGoBackListener()
        initDeleteTaskListener()
        initMarkTaskListener()
        initMoreOptionsListener()
    }

    private fun initMoreOptionsListener() {
        binding.includeTaskActions.moreOptions.setOnClickListener {
            bundle.putSerializable("todo_data", presetTodo)
            val todoMoveToBottomSheetModal = TodoMoveToBottomSheetModal()
            todoMoveToBottomSheetModal.arguments = bundle
            todoMoveToBottomSheetModal.show(
                supportFragmentManager,
                TodoMoveToBottomSheetModal::class.simpleName
            )
        }
    }

    private fun initMarkTaskListener() {
        binding.includeTaskActions.markTodo.setOnClickListener {
            requestMarkTask()
        }
    }

    private fun requestMarkTask() {
        val state = if (presetTodo.state) 0 else 1
        viewModel.markTask(presetTodo.taskId, state)
        val intent = Intent()
        intent.putExtra("requestCode", REQUEST_MARK_TODO_CODE)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun initDeleteTaskListener() {
        binding.todoDelete.setOnClickListener {
            requestDeleteTask()
        }
    }

    private fun requestDeleteTask() {
        viewModel.deleteTask(presetTodo)
        val intent = Intent()
        intent.putExtra("requestCode", REQUEST_DELETE_TODO_CODE)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun initGoBackListener() {
        binding.goBack.setOnClickListener {
            requestSaveTodo(presetTodo)
        }
    }

    private fun requestSaveTodo(task: Task) {
        if (validTitle()) {
            createAndSave(task)
        } else {
            Toast.makeText(this, getString(R.string.todo_title_required), Toast.LENGTH_SHORT).show()
        }
    }

    private fun createAndSave(task: Task) {
        val newTask = getNewTask(task)
        viewModel.saveAndRefresh(newTask)
        val intent = Intent()
        intent.putExtra("requestCode", REQUEST_SAVE_TODO_CODE)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun getNewTask(task: Task): Task {
        return task.copy(
            title = binding.todoTitle.text.toString(),
            details = binding.todoDetails.text.toString(),
            createdAt = binding.todoCreatedAt.text.toString(),
            priority = binding.todoPriority.isChecked,
            todoListId = todoListIdentifier,
        )
    }

    private fun validTitle(): Boolean {
        return binding.todoTitle.text.isNotEmpty()
    }

    override fun onBackPressed() {
        requestSaveTodo(presetTodo)
    }

    companion object {
        const val REQUEST_DELETE_TODO_CODE = 1
        const val REQUEST_MARK_TODO_CODE = 2
        const val REQUEST_SAVE_TODO_CODE = 3
    }

    override fun onMoveListener(requestCode: Int, identifier: Int) {
        if (isMovingAction(requestCode)) {
            todoListIdentifier = identifier
        }
    }

    private fun isMovingAction(requestCode: Int): Boolean {
        return requestCode == TodoMoveToBottomSheetModal.REQUEST_MOVE_TASK_CODE
    }
}
