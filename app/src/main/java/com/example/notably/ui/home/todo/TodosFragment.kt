package com.example.notably.ui.home.todo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.TasksAdapter
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentTodosBinding
import com.example.notably.extensions.Helper
import com.example.notably.repos.entities.Task
import com.example.notably.ui.home.HomeViewModel
import com.example.notably.ui.sheets.AddTodoBottomSheetModal
import com.example.notably.ui.sheets.TodoListsBottomSheetModal
import com.example.notably.ui.sheets.TodoListsBottomSheetModal.Companion.REQUEST_UPDATE_LIST_CODE
import com.example.notably.ui.sheets.TodoMoreOptionsBottomSheetModal
import com.example.notably.ui.sheets.TodoMoveToBottomSheetModal
import dagger.hilt.android.AndroidEntryPoint

@RequiresApi(Build.VERSION_CODES.M)
@AndroidEntryPoint
class TodosFragment : BaseFragment<HomeViewModel, FragmentTodosBinding>(),
    TasksAdapter.TaskListener {

    override val layoutId: Int = R.layout.fragment_todos

    override val viewModel: HomeViewModel by activityViewModels()

    private val tasksAdapter: TasksAdapter = TasksAdapter(this)

    private val bundle = Bundle()

    private var navigationHide = false

    override fun initComponents() {
        initTasksRcl()
        initTasksObserver()
        initTasks()
    }

    private fun initTasks() {
        viewModel.getTasks()
    }

    private fun initTasksObserver() {
        viewModel.tasks.observe(viewLifecycleOwner) {
            tasksAdapter.submitList(it)
            binding.todosEmptyPlaceholder.root.visibility =
                if (it.isNullOrEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun initTasksRcl() {
        binding.todosRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = tasksAdapter
        }
    }

    override fun initListeners() {
        initAddTodoListener()
        initScrollTasksListener()
        initTodoListsListener()
        initMoreOptionsListener()
    }

    private fun initMoreOptionsListener() {
        binding.moreOptions.setOnClickListener {
            val todoMoreOptionsBottomSheetModal = TodoMoreOptionsBottomSheetModal()
            todoMoreOptionsBottomSheetModal.setTargetFragment(this, REQUEST_MORE_OPTIONS_CODE)
            todoMoreOptionsBottomSheetModal.show(
                requireFragmentManager(),
                TodoMoreOptionsBottomSheetModal::class.simpleName
            )
        }
    }

    private fun initTodoListsListener() {
        binding.todoLists.setOnClickListener {
            requestViewTodoLists()
        }
    }

    private fun requestViewTodoLists() {
        val todoListsBottomSheetModal = TodoListsBottomSheetModal()
        todoListsBottomSheetModal.setTargetFragment(this, REQUEST_TODO_LISTS_CODE)
        todoListsBottomSheetModal.show(
            requireFragmentManager(),
            TodoListsBottomSheetModal::class.simpleName
        )
    }

    private fun initScrollTasksListener() {
        binding.nestedScrollview.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
            if (scrollY >= oldScrollY) {
                hideBottomOptions()
            } else {
                showBottomOptions()
            }
        }
    }

    private fun showBottomOptions() {
        if (!navigationHide) return
        Helper.show_bottom(binding.moreOptions)
        Helper.show_bottom(binding.todoLists)
        navigationHide = false
    }

    private fun hideBottomOptions() {
        if (navigationHide) return
        Helper.hide_bottom(binding.moreOptions)
        Helper.hide_bottom(binding.todoLists)
        navigationHide = true
    }

    private fun initAddTodoListener() {
        binding.addTodo.setOnClickListener {
            val addTodoBottomSheetModal = AddTodoBottomSheetModal()
            addTodoBottomSheetModal.setTargetFragment(this, REQUEST_ADD_TODO_CODE)
            addTodoBottomSheetModal.show(
                requireFragmentManager(),
                AddTodoBottomSheetModal::class.simpleName
            )
        }
    }

    override fun onTaskClicked(task: Task, position: Int) {
        val intent = Intent(context, ViewTodoActivity::class.java)
        intent.apply {
            putExtra("modifier", true)
            putExtra("todo", task)
        }
        startActivityForResult(intent, REQUEST_VIEW_TODO_CODE)
    }

    override fun onTaskLongClicked(task: Task, position: Int): Boolean {
        return false
    }

    override fun onTaskStateClicked(task: Task, position: Int, checked: Boolean) {
        task.state = checked
        requestUpdateTask(task)
    }

    private fun requestUpdateTask(task: Task) {
        viewModel.saveTask(task)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestViewSuccess(requestCode, resultCode) -> {
                when {
                    isUpdateAction(data) -> {
                        viewModel.getTasks()
                    }
                    isDeleteAction(data) -> {
                        viewModel.getTasks()
                    }
                    isMarkAction(data) -> {
                        viewModel.getTasks()
                    }
                }
            }
            requestTodosListSuccess(requestCode, resultCode) -> {
                viewModel.getTasks()
            }
            requestCode == REQUEST_MORE_OPTIONS_CODE -> {
                when (resultCode) {
                    TodoMoreOptionsBottomSheetModal.REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE -> {

                    }
                    TodoMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_DEFAULT -> {
                        viewModel.getTasks()
                    }
                    TodoMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_A_TO_Z -> {
                        viewModel.getTasks("a_z")
                    }
                    TodoMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_Z_TO_A -> {
                        viewModel.getTasks("z_a")
                    }
                }
            }
        }
    }

    private fun requestTodosListSuccess(requestCode: Int, resultCode: Int): Boolean {
        return requestCode == REQUEST_TODO_LISTS_CODE && resultCode == REQUEST_UPDATE_LIST_CODE
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
        const val REQUEST_ADD_TODO_CODE = 1
        const val REQUEST_MORE_OPTIONS_CODE = 4
        const val REQUEST_TODO_LISTS_CODE = 5
    }
}
