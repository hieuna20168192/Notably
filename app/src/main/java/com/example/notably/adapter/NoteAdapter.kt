package com.example.notably.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.R
import com.example.notably.databinding.LayoutItemNoteBinding
import com.example.notably.repos.entities.Note
import com.example.notably.repos.entities.NoteDiff

class NoteAdapter(
    private val notesListener: NotesListener
) : ListAdapter<Note, NoteAdapter.NoteViewHolder>(NoteDiff) {

    private val selectedItems = SparseBooleanArray()
    private var currentSelectedIndex = -1

    interface NotesListener {
        fun onNoteClicked(note: Note, pos: Int)
        fun onNoteLongClicked(note: Note, pos: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = LayoutItemNoteBinding.inflate(layoutInflater, parent, false)
        return NoteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        return holder.bind(note, position)
    }

    fun toggleSelection(position: Int) {
        currentSelectedIndex = position
        if (hasSelectedItem(position)) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        notifyItemChanged(position)
    }

    fun clearSelection() {
        selectedItems.clear()
        notifyDataSetChanged()
    }

    fun getSelectedItemCount() = selectedItems.size()

    private fun resetCurrentIndex() {
        currentSelectedIndex = -1
    }

    inner class NoteViewHolder(private val binding: LayoutItemNoteBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(note: Note, position: Int) {
            binding.run {
                this.note = note
                this.pos = position
                this.listener = notesListener
                setNoteImage(binding, note)
                toggleCheckedItem(binding, position)
                bindNoteColor(binding, note)
                executePendingBindings()
            }
        }

        private fun setNoteImage(binding: LayoutItemNoteBinding, note: Note) {
            if (hasImagePath(note)) {
                binding.itemNoteImage.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                binding.itemNoteImage.visibility = View.VISIBLE
            } else {
                binding.itemNoteImage.setImageBitmap(null)
                binding.itemNoteImage.visibility = View.GONE
            }
        }

        private fun hasImagePath(note: Note): Boolean {
            return note.imagePath.trim().isNotEmpty()
        }

        private fun toggleCheckedItem(binding: LayoutItemNoteBinding, position: Int) {
            val bgRes =
                if (hasSelectedItem(position)) R.drawable.note_background_border else R.drawable.note_background
            binding.itemNoteLayout.setBackgroundResource(bgRes)

            if (currentSelectedIndex == position) {
                resetCurrentIndex()
            }
        }

        private fun bindNoteColor(binding: LayoutItemNoteBinding, note: Note) {
            val shape = binding.itemNoteLayout.background as GradientDrawable
            val color =
                if (!note.color.isNullOrEmpty()) Color.parseColor(note.color) else Color.parseColor(
                    "#ebebeb"
                )
            shape.setColor(color)
        }
    }

    private fun hasSelectedItem(position: Int): Boolean {
        return selectedItems.get(position, false)
    }
}
