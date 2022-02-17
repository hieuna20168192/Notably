package com.example.notably.ui.sheets.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.ChooseCategoryAdapter
import com.example.notably.databinding.CategoriesBottomSheetModalBinding
import com.example.notably.repos.entities.Category
import com.example.notably.ui.edit.category.EditCategoryActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ClassCastException

@AndroidEntryPoint
class CategoriesBottomSheetModal : BottomSheetDialogFragment(),
    ChooseCategoryAdapter.ChooseCategoryListener {

    interface OnChooseListener {
        fun onChooseListener(requestCode: Int, category: Category)
    }

    private val viewModel by viewModels<CategoryViewModel>()

    private lateinit var binding: CategoriesBottomSheetModalBinding

    private lateinit var adapter: ChooseCategoryAdapter

    private lateinit var onChooseListener: OnChooseListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            onChooseListener = context as OnChooseListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement OnChooseListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.categories_bottom_sheet_modal,
            container,
            false
        )
        binding.lifecycleOwner = this
        initComponents()
        initListeners()
        return binding.root
    }

    private fun initComponents() {
        initCategoriesAdapter()
        initCategoriesRcl()
        initCategories()
        initCategoriesObserver()
    }

    private fun initCategoriesObserver() {
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            adapter.submitList(categories)
        }
    }

    private fun initCategories() {
        viewModel.getCategories()
    }

    private fun initCategoriesAdapter() {
        adapter = ChooseCategoryAdapter(this)
    }

    private fun initCategoriesRcl() {
        binding.categoriesRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = this@CategoriesBottomSheetModal.adapter
        }
    }

    private fun initListeners() {
        initAddCategoriesListener()
    }

    private fun initAddCategoriesListener() {
        binding.addCategory.setOnClickListener {
            requestEditCategory()
            dismiss()
        }
    }

    private fun requestEditCategory() {
        startActivity(Intent(requireContext(), EditCategoryActivity::class.java))
    }

    override fun onCategoryClicked(category: Category, position: Int) {
        onChooseListener.onChooseListener(REQUEST_CHOOSE_CATEGORY_CODE, category)
        dismiss()
    }

    companion object {
        const val REQUEST_CHOOSE_CATEGORY_CODE = 5
    }
}
