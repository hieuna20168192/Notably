package com.example.notably.ui.sheets

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.TodoListAdapter
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.FragmentTodoMoveToBottomSheetModalBinding
import com.example.notably.repos.entities.TodosList
import com.example.notably.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ClassCastException

@AndroidEntryPoint
class TodoMoveToBottomSheetModal :
    BaseBottomSheet<HomeViewModel, FragmentTodoMoveToBottomSheetModalBinding>(),
    TodoListAdapter.TodoListListener {

    override val layoutId: Int = R.layout.fragment_todo_move_to_bottom_sheet_modal

    override val viewModel: HomeViewModel by activityViewModels()

    private val todoListAdapter: TodoListAdapter = TodoListAdapter(this)

    private lateinit var onMoveListener: OnMoveListener

    interface OnMoveListener {
        fun onMoveListener(requestCode: Int, identifier: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onMoveListener = context as OnMoveListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement onMoveListener")
        }
    }

    override fun initComponents() {
        initTodosRcl()
        initTodosList()
        initTodosListObserver()
    }

    private fun initTodosListObserver() {
        viewModel.todosLists.observe(this) {
            todoListAdapter.submitList(it)
        }
    }

    private fun initTodosList() {
        viewModel.getTodosLists()
    }

    private fun initTodosRcl() {
        binding.todoListRecyclerview.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.todoListRecyclerview.adapter = todoListAdapter
    }

    override fun initListeners() {

    }

    override fun onTodoListClicked(todosList: TodosList, position: Int) {
        onMoveListener.onMoveListener(REQUEST_MOVE_TASK_CODE, todosList.identifier)
        Toast.makeText(
            context,
            getString(R.string.todo_moved_to) + " " + todosList.title,
            Toast.LENGTH_SHORT
        ).show()
        dismiss()
    }

    companion object {
        const val REQUEST_MOVE_TASK_CODE = 1
    }
}
