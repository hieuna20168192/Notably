package com.example.notably.ui.edit.category

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.example.notably.R
import com.example.notably.adapter.CategoriesAdapter
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityEditCategoryBinding
import com.example.notably.repos.entities.Category
import com.example.notably.ui.main.MainActivity
import com.example.notably.ui.sheets.category.AddCategoryBottomSheetModal
import com.example.notably.ui.sheets.category.AddCategoryBottomSheetModal.Companion.REQUEST_ADD_CATEGORY_CODE
import com.example.notably.ui.sheets.category.CategoryViewModel
import com.example.notably.ui.sheets.category.EditCategoryBottomSheetModal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditCategoryActivity : BaseActivity<CategoryViewModel, ActivityEditCategoryBinding>(),
    CategoriesAdapter.CategoriesListener,
    AddCategoryBottomSheetModal.OnAddListener {

    override val layoutId: Int = R.layout.activity_edit_category

    override val viewModel: CategoryViewModel by viewModels()

    private val bundle = Bundle()
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun initComponents() {
        initCategoriesAdapter()
        initCategoriesRcl()
        initCategoriesObserver()
        initCategories()
    }

    private fun initCategories() {
        viewModel.getCategories()
    }

    private fun initCategoriesObserver() {
        viewModel.categories.observe(this) {
            categoriesAdapter.submitList(it)
        }
    }

    private fun initCategoriesRcl() {
        binding.categoriesRecyclerview.adapter = categoriesAdapter
    }

    private fun initCategoriesAdapter() {
        categoriesAdapter = CategoriesAdapter(this)
    }

    override fun initListeners() {
        initGoBackListener()
        initAddCategoryListener()
    }

    private fun initAddCategoryListener() {
        binding.addCategory.setOnClickListener {
            val addCategoryBottomSheetModal = AddCategoryBottomSheetModal()
            addCategoryBottomSheetModal.show(
                supportFragmentManager,
                addCategoryBottomSheetModal::class.simpleName
            )
        }
    }

    private fun initGoBackListener() {
        binding.goBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onCategoryDeleteClicked(category: Category, position: Int) {
        viewModel.deleteAndRefreshCategories(category)
        Toast.makeText(this, getString(R.string.category_deleted), Toast.LENGTH_SHORT).show()
    }

    override fun onCategoryEditClicked(category: Category, position: Int) {
        bundle.putSerializable("preset_category", category)

        // request editCategory
        val editCategoryBottomSheetModal = EditCategoryBottomSheetModal()
        editCategoryBottomSheetModal.apply {
            arguments = bundle
            show(supportFragmentManager, EditCategoryBottomSheetModal::class.simpleName)
        }
    }

    override fun onAddListener(requestCode: Int) {
        if (requestCode == REQUEST_ADD_CATEGORY_CODE) {
//            viewModel.getCategories()
            Toast.makeText(this, getString(R.string.category_added), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
