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
import com.example.notably.databinding.LayoutItemNoteTrashBinding
import com.example.notably.repos.entities.TrashNote
import com.example.notably.repos.entities.TrashNoteDiff

class TrashNoteAdapter(
    private val notesListener: TrashNoteListener
) : ListAdapter<TrashNote, TrashNoteAdapter.TrashNoteViewHolder>(TrashNoteDiff) {

    interface TrashNoteListener {
        fun onTrashNoteClicked(note: TrashNote, pos: Int)
        fun onTrashNoteLongClicked(note: TrashNote, pos: Int): Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashNoteViewHolder {
        return TrashNoteViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: TrashNoteViewHolder, position: Int) {
        val note = getItem(position)
        return holder.bind(notesListener, note, position)
    }

    class TrashNoteViewHolder private constructor(private val binding: LayoutItemNoteTrashBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notesListener: TrashNoteListener, note: TrashNote, position: Int) {
            binding.run {
                setTrashNoteListener(binding, notesListener, note, position)
                bindTrashNoteContent(binding, note)
                setNoteImage(binding, note)
                bindNoteColor(binding, note)
                executePendingBindings()
            }
        }

        private fun bindTrashNoteContent(binding: LayoutItemNoteTrashBinding, note: TrashNote) {
            binding.itemNoteTitle.text = note.title
            binding.itemNoteSubtitle.text = note.subtitle
            binding.itemNoteSubtitle.isGone = note.subtitle.isEmpty()
            binding.itemNoteCreatedAt.text = note.createdAt
        }

        private fun setTrashNoteListener(
            binding: LayoutItemNoteTrashBinding,
            listener: TrashNoteListener,
            note: TrashNote,
            position: Int
        ) {
            binding.root.setOnClickListener { listener.onTrashNoteClicked(note, position) }
            binding.root.setOnLongClickListener { listener.onTrashNoteLongClicked(note, position) }
        }

        private fun setNoteImage(binding: LayoutItemNoteTrashBinding, note: TrashNote) {
            if (hasImagePath(note)) {
                binding.itemNoteImage.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                binding.itemNoteImage.visibility = View.VISIBLE
            } else {
                binding.itemNoteImage.setImageBitmap(null)
                binding.itemNoteImage.visibility = View.GONE
            }
        }

        private fun hasImagePath(note: TrashNote): Boolean {
            return note.imagePath.trim().isNotEmpty()
        }

        private fun bindNoteColor(binding: LayoutItemNoteTrashBinding, note: TrashNote) {
            val shape = binding.itemNoteLayout.background as GradientDrawable
            val color =
                if (note.color != null) Color.parseColor(note.color) else Color.parseColor("#ebebeb")
            shape.setColor(color)
        }

        companion object {
            fun from(parent: ViewGroup): TrashNoteViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = LayoutItemNoteTrashBinding.inflate(layoutInflater, parent, false)
                return TrashNoteViewHolder(binding)
            }
        }
    }
}
