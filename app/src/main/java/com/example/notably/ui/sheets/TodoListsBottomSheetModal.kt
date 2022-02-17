package com.example.notably.ui.sheets

import android.content.Intent
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.TodoListAdapter
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.FragmentTodoListsBottomSheetModalBinding
import com.example.notably.repos.entities.TodosList
import com.example.notably.ui.home.HomeViewModel
import com.example.notably.ui.home.todo.TodoCreateListActivity
import com.example.notably.ui.home.todo.TodoListActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoListsBottomSheetModal :
    BaseBottomSheet<HomeViewModel, FragmentTodoListsBottomSheetModalBinding>(),
    TodoListAdapter.TodoListListener {

    override val layoutId: Int = R.layout.fragment_todo_lists_bottom_sheet_modal

    override val viewModel: HomeViewModel by activityViewModels()

    private val todoListAdapter = TodoListAdapter(this)

    override fun initComponents() {
        initTodosListRcl()
        initTodosListsObserver()
        initTodosLists()
    }

    private fun initTodosListsObserver() {
        viewModel.todosLists.observe(viewLifecycleOwner) {
            todoListAdapter.submitList(it)
        }
    }

    private fun initTodosLists() {
        viewModel.getTodosLists()
    }

    private fun initTodosListRcl() {
        binding.todoListRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = todoListAdapter
        }
    }

    override fun initListeners() {
        initCreateNewListListener()
    }

    private fun initCreateNewListListener() {
        binding.createList.setOnClickListener {
            startActivityForResult(
                Intent(context, TodoCreateListActivity::class.java),
                REQUEST_ADD_LIST_CODE
            )
        }
    }

    override fun onTodoListClicked(todosList: TodosList, position: Int) {
        requestViewTodoList(todosList)
    }

    private fun requestViewTodoList(todosList: TodosList) {
        val intent = Intent(context, TodoListActivity::class.java)
        intent.putExtra("todo_list", todosList)
        startActivityForResult(intent, REQUEST_UPDATE_LIST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (isCreateNewRequest(requestCode, data)) {
            viewModel.getTodosLists()
        } else if (isUpdateRequest(requestCode, data)) {
            dismiss()
            val intent = Intent()
            targetFragment?.onActivityResult(targetRequestCode, REQUEST_UPDATE_LIST_CODE, intent)
        }
    }

    private fun isUpdateRequest(requestCode: Int, data: Intent?): Boolean {
        return requestCode == REQUEST_UPDATE_LIST_CODE && data != null && data.getBooleanExtra(
            "is_updated",
            false
        )
    }

    private fun isCreateNewRequest(requestCode: Int, data: Intent?): Boolean {
        return requestCode == TodoCreateListActivity.REQUEST_CREATE_LIST_CODE &&
                data != null && data.getBooleanExtra("is_added", false)
    }

    companion object {
        const val REQUEST_ADD_LIST_CODE = 1
        const val REQUEST_UPDATE_LIST_CODE = 2
    }
}
