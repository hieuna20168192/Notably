package com.example.notably.ui.sheets

import android.content.Intent
import androidx.fragment.app.viewModels
import com.example.notably.R
import com.example.notably.base.BaseBottomSheet
import com.example.notably.databinding.NoteActionsBottomSheetModalBinding
import com.example.notably.repos.entities.Note
import com.example.notably.ui.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteActionsBottomSheetModal :
    BaseBottomSheet<HomeViewModel, NoteActionsBottomSheetModalBinding>() {

    override val layoutId: Int = R.layout.note_actions_bottom_sheet_modal

    override val viewModel: HomeViewModel by viewModels()

    private lateinit var note: Note

    override fun initComponents() {
        initPresetNote()
    }

    private fun initPresetNote() {
        note = requireArguments().getSerializable("note_data") as Note
    }

    override fun initListeners() {
        initMoveToTrashListener()
    }

    private fun initMoveToTrashListener() {
        binding.moveToTrash.setOnClickListener {
            viewModel.insertTrashNote(note)
            sendResult(REQUEST_DELETE_NOTE_CODE)
        }
    }

    private fun sendResult(REQUEST_CODE: Int) {
        targetFragment?.onActivityResult(targetRequestCode, REQUEST_CODE, Intent())
        dismiss()
    }

    companion object {
        const val REQUEST_DELETE_NOTE_CODE = 3
    }
}
