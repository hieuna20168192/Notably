package com.example.notably.ui.sheets

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.TodoMoreOptionsBottomSheetModalBinding
import com.example.notably.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoMoreOptionsBottomSheetModal :
    BaseBottomSheet<HomeViewModel, TodoMoreOptionsBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.todo_more_options_bottom_sheet_modal

    override val viewModel: HomeViewModel by activityViewModels()

    override fun initComponents() {
        initNumOfCompletedTasksObserver()
        initNumOfCompletedTasks()

    }

    private fun initNumOfCompletedTasks() {
        viewModel.getNumOfCompletedTasks()
    }

    private fun initNumOfCompletedTasksObserver() {
        viewModel.numOfCompletedTasks.observe(viewLifecycleOwner) { numOfTasks ->
            binding.deleteAllCompletedTasks.isGone = numOfTasks <= 0
        }
    }

    override fun initListeners() {
        initSortingListener()
        initDeleteAllCompletedTasksListener()
    }

    private fun initDeleteAllCompletedTasksListener() {
        binding.deleteAllCompletedTasks.setOnClickListener {
            requestDeleteAllCompletedTasks()
        }
    }

    private fun requestDeleteAllCompletedTasks() {
        val confirmDialog = Dialog(requireContext())

        // Set style for dialog
        confirmDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_confirm)
            setCancelable(true)
            setOnCancelListener { dismiss() }
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            }
        }

        val confirmHeader = confirmDialog.findViewById<TextView>(R.id.confirm_header)
        val confirmText = confirmDialog.findViewById<TextView>(R.id.confirm_text)
        val confirmAllow = confirmDialog.findViewById<TextView>(R.id.confirm_allow)
        val confirmCancel = confirmDialog.findViewById<TextView>(R.id.confirm_deny)

        confirmHeader.text = getString(R.string.delete_all_completed_tasks_header)

        confirmText.text =
            "${viewModel.numOfCompletedTasks.value} ${getString(R.string.delete_all_completed_tasks_description)}"

        confirmAllow.text = getString(R.string.delete)
        confirmAllow.setOnClickListener {
            viewModel.deleteAllCompletedTasksAndRefresh()
            sendResult(REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE)
            confirmDialog.dismiss()
            dismiss()
        }

        confirmCancel.setOnClickListener { confirmDialog.dismiss() }

        confirmDialog.show()
    }

    private fun initSortingListener() {
        binding.sortAToZ.setOnClickListener {
            requestSortDialog()
        }
    }

    private fun requestSortDialog() {
        val sortByDialog = Dialog(requireContext())
        sortByDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_sort_by)
            setCancelable(true)
            setOnCancelListener { dismiss() }
        }

        // sort by default
        val sortDefault = sortByDialog.findViewById<LinearLayout>(R.id.sort_by_default)
        sortDefault.setOnClickListener {
            sendResult(CHOOSE_SORT_BY_DEFAULT)
            sortByDialog.dismiss()
        }

        // sort by name a - z
        val aToZ = sortByDialog.findViewById<LinearLayout>(R.id.sort_a_to_z)
        aToZ.setOnClickListener {
            sendResult(CHOOSE_SORT_BY_A_TO_Z)
            sortByDialog.dismiss()
        }

        // sort by name z - a
        val zToA = sortByDialog.findViewById<LinearLayout>(R.id.sort_z_to_a)
        zToA.setOnClickListener {
            sendResult(CHOOSE_SORT_BY_Z_TO_A)
            sortByDialog.dismiss()
        }

        // confirm cancel
        val confirmCancel = sortByDialog.findViewById<LinearLayout>(R.id.confirm_deny)
        confirmCancel.setOnClickListener { sortByDialog.dismiss() }

        sortByDialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            attributes.windowAnimations = R.style.DetailAnimationFade
        }
        sortByDialog.show()
        dismiss()
    }

    private fun sendResult(requestCode: Int) {
        targetFragment?.onActivityResult(
            targetRequestCode,
            requestCode,
            Intent()
        )
        dismiss()
    }

    companion object {
        const val REQUEST_DELETE_ALL_COMPLETED_TASKS_CODE = 1
        const val CHOOSE_SORT_BY_A_TO_Z = 2
        const val CHOOSE_SORT_BY_Z_TO_A = 3
        const val CHOOSE_SORT_BY_DEFAULT = 4
    }
}
