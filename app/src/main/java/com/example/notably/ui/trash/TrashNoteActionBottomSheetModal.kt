package com.example.notably.ui.trash

import androidx.fragment.app.activityViewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.TrashNoteActionBottomSheetModalBinding
import com.example.notably.repos.entities.TrashNote
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrashNoteActionBottomSheetModal :
    BaseBottomSheet<TrashViewModel, TrashNoteActionBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.trash_note_action_bottom_sheet_modal

    override val viewModel: TrashViewModel by activityViewModels()

    private lateinit var trashNote: TrashNote

    override fun initComponents() {
        initPresetTrashNote()
    }

    private fun initPresetTrashNote() {
        trashNote = requireArguments().getSerializable("trash_note_data") as TrashNote
    }

    override fun initListeners() {
        initRestoreNoteListener()
        initDeleteNoteListener()
    }

    private fun initDeleteNoteListener() {
        binding.deleteNote.setOnClickListener {
            viewModel.deleteTrashNote(trashNote)
            dismiss()
        }
    }

    private fun initRestoreNoteListener() {
        binding.restoreNote.setOnClickListener {
            viewModel.restoreNote(trashNote)
            dismiss()
        }
    }
}
