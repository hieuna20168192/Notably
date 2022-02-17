package com.example.notably.ui.sheets.category

import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.EditCategoryBottomSheetModalBinding
import com.example.notably.repos.entities.Category

class EditCategoryBottomSheetModal :
    BaseBottomSheet<CategoryViewModel, EditCategoryBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.edit_category_bottom_sheet_modal

    override val viewModel: CategoryViewModel by activityViewModels()

    private var presetCategory: Category? = null

    override fun initComponents() {
        initPresetCategory()
    }

    private fun initPresetCategory() {
        presetCategory = requireArguments().getSerializable("preset_category") as Category
    }

    override fun initListeners() {
        initCategoryTitleChange()
        initEditCategoryListener()
    }

    private fun initEditCategoryListener() {
        binding.editCategory.setOnClickListener {
            if (hasMandatoryTitle()) {
                val editedCategory =
                    presetCategory?.copy(title = binding.categoryTitle.text.toString())
                editedCategory?.let {
                    viewModel.saveAndRefreshCategories(it)
                }
                dismiss()
            }
        }
    }

    private fun initCategoryTitleChange() {
        binding.categoryTitle.doOnTextChanged { _, _, _, _ ->
            binding.editCategory.isEnabled = hasMandatoryTitle()
        }
    }

    private fun hasMandatoryTitle(): Boolean {
        return binding.categoryTitle.text.trim().isNotEmpty()
    }
}
