package com.example.notably.adapter

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notably.databinding.LayoutItemNoteArchiveBinding
import com.example.notably.repos.entities.ArchiveNote
import com.example.notably.repos.entities.ArchiveNoteDiff

class ArchiveNotesAdapter(
    private val notesListener: ArchiveNoteListener
) : ListAdapter<ArchiveNote, ArchiveNotesAdapter.ArchiveNoteViewHolder>(ArchiveNoteDiff) {

    interface ArchiveNoteListener {
        fun onArchiveNoteClicked(note: ArchiveNote, pos: Int)
        fun onArchiveNoteLongClicked(note: ArchiveNote, pos: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArchiveNoteViewHolder {
        return ArchiveNoteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ArchiveNoteViewHolder, position: Int) {
        val note = getItem(position)
        return holder.bind(notesListener, note, position)
    }

    class ArchiveNoteViewHolder private constructor(private val binding: LayoutItemNoteArchiveBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notesListener: ArchiveNoteListener, note: ArchiveNote, position: Int) {
            binding.run {
                setTrashNoteListener(binding, notesListener, note, position)
                bindTrashNoteContent(binding, note)
                setNoteImage(binding, note)
                bindNoteColor(binding, note)
                executePendingBindings()
            }
        }

        private fun bindTrashNoteContent(binding: LayoutItemNoteArchiveBinding, note: ArchiveNote) {
            binding.itemNoteTitle.text = note.title
            binding.itemNoteSubtitle.text = note.subtitle
            binding.itemNoteSubtitle.isGone = note.subtitle.isEmpty()
            binding.itemNoteCreatedAt.text = note.createdAt
        }

        private fun setTrashNoteListener(
            binding: LayoutItemNoteArchiveBinding,
            listener: ArchiveNoteListener,
            note: ArchiveNote,
            position: Int
        ) {
            binding.root.setOnClickListener { listener.onArchiveNoteClicked(note, position) }
            binding.root.setOnLongClickListener {
                listener.onArchiveNoteLongClicked(
                    note,
                    position
                )
            }
        }

        private fun setNoteImage(binding: LayoutItemNoteArchiveBinding, note: ArchiveNote) {
            if (hasImagePath(note)) {
                binding.itemNoteImage.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                binding.itemNoteImage.visibility = View.VISIBLE
            } else {
                binding.itemNoteImage.setImageBitmap(null)
                binding.itemNoteImage.visibility = View.GONE
            }
        }

        private fun hasImagePath(note: ArchiveNote): Boolean {
            return note.imagePath.trim().isNotEmpty()
        }

        private fun bindNoteColor(binding: LayoutItemNoteArchiveBinding, note: ArchiveNote) {
            val shape = binding.itemNoteLayout.background as GradientDrawable
            val color =
                if (note.color != null) Color.parseColor(note.color) else Color.parseColor("#ebebeb")
            shape.setColor(color)
        }

        companion object {
            fun from(parent: ViewGroup): ArchiveNoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemNoteArchiveBinding.inflate(layoutInflater, parent, false)
                return ArchiveNoteViewHolder(binding)
            }
        }
    }
}
