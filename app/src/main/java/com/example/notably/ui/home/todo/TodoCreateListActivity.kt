package com.example.notably.ui.home.todo

import android.content.Intent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import com.example.notably.R
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityTodoCreateListBinding
import com.example.notably.repos.entities.TodosList
import com.example.notably.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TodoCreateListActivity : BaseActivity<HomeViewModel, ActivityTodoCreateListBinding>() {

    override val layoutId: Int = R.layout.activity_todo_create_list

    override val viewModel: HomeViewModel by viewModels()

    override fun initComponents() {
        showKeyBoard()
    }

    override fun initListeners() {
        initGoBackListener()
        initListTitleListener()
        initSaveListListener()
    }

    private fun initSaveListListener() {
        binding.todoListSave.setOnClickListener {
            if (hasListTitle()) {
                requestSaveList()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.todo_list_title_required),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun requestSaveList() {
        hideKeyBoard()
        val date = Date()
        val identifier = SimpleDateFormat("ddHHmmss", Locale.US).format(date).toInt()
        val title = binding.todoListTitle.text
        val newTodosList = TodosList(
            identifier = identifier,
            title = title.toString()
        )
        viewModel.saveTodosList(newTodosList)
        val intent = Intent()
        intent.putExtra("is_added", true)
        setResult(REQUEST_CREATE_LIST_CODE, intent)
        finish()
    }

    private fun hasListTitle(): Boolean {
        return binding.todoListTitle.text.isNotEmpty()
    }

    private fun initListTitleListener() {
        binding.todoListTitle.requestFocus()
        binding.todoListTitle.doOnTextChanged { text, _, _, _ ->
            binding.todoListSave.isEnabled = text?.isNotEmpty() ?: false
        }
    }

    private fun initGoBackListener() {
        binding.goBack.setOnClickListener {
            finish()
        }
    }

    companion object {
        const val REQUEST_CREATE_LIST_CODE = 1
    }
}
