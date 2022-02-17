package com.example.notably.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.LayoutChooseCategoryBinding
import com.example.notably.repos.entities.Category
import com.example.notably.repos.entities.CategoryDiff

class ChooseCategoryAdapter(
    private val chooseCategoryListener: ChooseCategoryListener
) : ListAdapter<Category, ChooseCategoryAdapter.CategoryViewHolder>(CategoryDiff) {

    interface ChooseCategoryListener {
        fun onCategoryClicked(category: Category, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        return holder.bind(chooseCategoryListener, category, position)
    }

    class CategoryViewHolder private constructor(private val binding: LayoutChooseCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: ChooseCategoryListener,
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
                val binding = LayoutChooseCategoryBinding.inflate(layoutInflater, parent, false)
                return CategoryViewHolder(binding)
            }
        }
    }
}
