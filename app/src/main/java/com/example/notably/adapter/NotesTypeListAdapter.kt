package com.example.notably.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.ItemNoteTypeListBinding
import com.example.notably.repos.entities.Note
import com.example.notably.repos.entities.NoteDiff

class NotesTypeListAdapter(
    private val notesListener: NoteAdapter.NotesListener
) : ListAdapter<Note, NotesTypeListAdapter.NoteViewHolder>(NoteDiff) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val note = getItem(position)
        return holder.bind(notesListener, note, position)
    }

    class NoteViewHolder private constructor(private val binding: ItemNoteTypeListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notesListener: NoteAdapter.NotesListener, note: Note, position: Int) {
            binding.run {
                this.note = note
                this.listener = notesListener
                this.pos = position
                setNoteImage(binding, note)
                bindNoteColor(binding, note)
                executePendingBindings()
            }
        }

        private fun setNoteImage(binding: ItemNoteTypeListBinding, note: Note) {
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

        private fun bindNoteColor(binding: ItemNoteTypeListBinding, note: Note) {
            val shape = binding.itemNoteLayout.background as GradientDrawable
            val color =
                if (note.color != null) Color.parseColor(note.color) else Color.parseColor("#ebebeb")
            shape.setColor(color)
        }

        companion object {
            fun from(parent: ViewGroup): NoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemNoteTypeListBinding.inflate(layoutInflater, parent, false)
                return NoteViewHolder(binding)
            }
        }
    }
}
