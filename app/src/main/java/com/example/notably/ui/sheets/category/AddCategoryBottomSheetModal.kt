package com.example.notably.ui.sheets.category

import android.content.Context
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.AddCategoryBottomSheetModalBinding
import com.example.notably.repos.entities.Category
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ClassCastException

@AndroidEntryPoint
class AddCategoryBottomSheetModal :
    BaseBottomSheet<CategoryViewModel, AddCategoryBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.add_category_bottom_sheet_modal

    override val viewModel: CategoryViewModel by activityViewModels()

    private lateinit var onAddListener: OnAddListener

    interface OnAddListener {
        fun onAddListener(requestCode: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onAddListener = context as OnAddListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement onAddListener")
        }
    }

    override fun initComponents() {

    }

    override fun initListeners() {
        initAddCategoryListener()
        initCategoryTitleListener()
    }

    private fun initCategoryTitleListener() {
        binding.categoryTitle.doOnTextChanged { _, _, _, _ ->
            binding.addCategory.isEnabled = hasMandatoryFields()
        }
    }

    private fun initAddCategoryListener() {
        binding.addCategory.setOnClickListener {
            if (hasMandatoryFields()) {
                val newCategory = Category(
                    title = binding.categoryTitle.text.toString(),
                    isPrimary = false
                )
                viewModel.saveAndRefreshCategories(newCategory)
                onAddListener.onAddListener(REQUEST_ADD_CATEGORY_CODE)
                dismiss()
            }
        }
    }

    private fun hasMandatoryFields(): Boolean {
        return binding.categoryTitle.text.trim().isNotEmpty()
    }

    companion object {
        const val REQUEST_ADD_CATEGORY_CODE = 3
    }
}
