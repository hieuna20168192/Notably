package com.example.notably.ui.archive

import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.ArchiveNoteActionBottomSheetModalBinding
import com.example.notably.repos.entities.ArchiveNote
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchiveNoteActionBottomSheetModal :
    BaseBottomSheet<ArchiveViewModel, ArchiveNoteActionBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.archive_note_action_bottom_sheet_modal

    override val viewModel: ArchiveViewModel by activityViewModels()

    private lateinit var archiveNote: ArchiveNote

    override fun initComponents() {
        archiveNote = requireArguments().getSerializable("archive_note_data") as ArchiveNote
    }

    override fun initListeners() {
        binding.unarchive.setOnClickListener {
            requestDeleteArchiveNote(archiveNote)
        }
    }

    private fun requestDeleteArchiveNote(archiveNote: ArchiveNote) {
        viewModel.deleteAndRefreshArchiveNote(archiveNote)
        Toast.makeText(context, getString(R.string.note_restored_from_archive), Toast.LENGTH_SHORT).show()
        dismiss()
    }
}
