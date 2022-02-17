package com.example.notably.repos.entities

import androidx.recyclerview.widget.DiffUtil
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "trash_notes")
data class TrashNote(
    @PrimaryKey(autoGenerate = true)
    val noteId: Int = 0,

    @ColumnInfo(name = "note_title")
    val title: String = "",

    @ColumnInfo(name = "note_created_at")
    val createdAt: String = "",

    @ColumnInfo(name = "note_subtitle")
    val subtitle: String = "",

    @ColumnInfo(name = "note_description")
    val description: String = "",

    @ColumnInfo(name = "note_image_path")
    val imagePath: String = "",

    @ColumnInfo(name = "note_color")
    val color: String = "",

    @ColumnInfo(name = "note_web_link")
    val webLink: String = "",

    @ColumnInfo(name = "note_category_id")
    val categoryId: Int = 0,

    @ColumnInfo(name = "note_reminder")
    val reminder: String = "",
) : Serializable {

    override fun toString(): String {
        return "$title : $createdAt"
    }
}

object TrashNoteDiff : DiffUtil.ItemCallback<TrashNote>() {
    override fun areItemsTheSame(oldItem: TrashNote, newItem: TrashNote): Boolean {
        return oldItem.noteId == newItem.noteId
    }

    override fun areContentsTheSame(oldItem: TrashNote, newItem: TrashNote): Boolean {
        return oldItem == newItem
    }
}
