package com.example.notably.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.LayoutItemCategoryBinding
import com.example.notably.repos.entities.Category
import com.example.notably.repos.entities.CategoryDiff

class CategoriesAdapter(
    private val categoryListener: CategoriesListener
) : ListAdapter<Category, CategoriesAdapter.CategoryViewHolder>(CategoryDiff) {

    interface CategoriesListener {
        fun onCategoryDeleteClicked(category: Category, position: Int)
        fun onCategoryEditClicked(category: Category, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(categoryListener, category, position)
    }

    class CategoryViewHolder private constructor(private val binding: LayoutItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: CategoriesListener,
            category: Category,
            position: Int
        ) {
            binding.run {
                this.listener = listener
                this.category = category
                this.pos = position
                executePendingBindings()
            }
        }

        companion object {
            fun from(parent: ViewGroup): CategoryViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemCategoryBinding.inflate(layoutInflater, parent, false)
                return CategoryViewHolder(binding)
            }
        }
    }
}
