package com.example.notably.repos.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    @ColumnInfo(name = "category_title")
    val title: String = "",
    @ColumnInfo(name = "category_is_primary")
    val isPrimary: Boolean = false,
) : Serializable {

    override fun toString(): String {
        return "$categoryId : $categoryId"
    }
}

object CategoryDiff : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem.categoryId == newItem.categoryId
    }

    override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
        return oldItem == newItem
    }
}
