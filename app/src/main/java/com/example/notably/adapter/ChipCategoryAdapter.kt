package com.example.notably.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.ItemCategoryFilterBinding
import com.example.notably.repos.entities.Category
import com.example.notably.repos.entities.CategoryDiff

class ChipCategoryAdapter(
    private val chipCategoryListener: ChipCategoryListener
) : ListAdapter<Category, ChipCategoryAdapter.CategoryViewHolder>(CategoryDiff) {

    interface ChipCategoryListener {
        fun onCategoryClicked(category: Category, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        return holder.bind(chipCategoryListener, category, position)
    }

    class CategoryViewHolder private constructor(private val binding: ItemCategoryFilterBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(
            listener: ChipCategoryListener,
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
                val binding = ItemCategoryFilterBinding.inflate(layoutInflater, parent, false)
                return CategoryViewHolder(binding)
            }
        }
    }
}
