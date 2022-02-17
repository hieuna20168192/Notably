package com.example.notably.ui.sheets

import android.app.Activity
import android.content.Intent
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.AddTodoBottomSheetModalBinding
import com.example.notably.extensions.Helper
import com.example.notably.repos.entities.Task
import com.example.notably.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class AddTodoBottomSheetModal : BaseBottomSheet<HomeViewModel, AddTodoBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.add_todo_bottom_sheet_modal

    override val viewModel: HomeViewModel by activityViewModels()

    override fun initComponents() {
        binding.todoTitle.requestFocus()
        showKeyBoard()
        applyCheckboxTint()
    }

    private fun applyCheckboxTint() {
        val isChecked = binding.todoPriority.isChecked
        if (isChecked) {
            Helper.set_background_tint(context, binding.todoPriority, R.color.color_danger)
        } else {
            Helper.set_background_tint(context, binding.todoPriority, R.color.color_dark)
        }
    }

    override fun initListeners() {
        initTitleListener()
        initPriorityListener()
        initAddTaskListener()
    }

    private fun initAddTaskListener() {
        binding.addTodo.setOnClickListener {
            if (hasTitle()) {
                saveTask()
            }
        }
    }

    private fun initPriorityListener() {
        binding.todoPriority.setOnCheckedChangeListener { _, _ ->
            applyCheckboxTint()
        }
    }

    private fun initTitleListener() {
        binding.todoTitle.doOnTextChanged { text, _, _, _ ->
            binding.addTodo.isEnabled = text?.isNotEmpty() ?: false
        }

        // on editor action listener
        binding.todoTitle.setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (hasTitle()) {
                    saveTask()
                } else {
                    Toast.makeText(
                        context,
                        getString(R.string.todo_title_required),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            false
        }
    }

    private fun saveTask() {
        val newTask = Task(
            title = binding.todoTitle.text.toString(),
            state = false,
            createdAt = SimpleDateFormat("MM.dd.yyyy, HH:mm a", Locale.getDefault()).format(Date()),
            priority = binding.todoPriority.isChecked,
            todoListId = 1
        )
        viewModel.saveAndRefresh(newTask)
        hideKeyBoard()
        val intent = Intent().apply {
            putExtra("is_added", true)
        }
        targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, intent)
        dismiss()
    }

    private fun hasTitle(): Boolean {
        return binding.todoTitle.text.isNotEmpty()
    }
}
