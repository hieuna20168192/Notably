package com.example.notably.ui.archive

import android.os.Bundle
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.ArchiveNotesAdapter
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentArchiveBinding
import com.example.notably.repos.entities.ArchiveNote
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArchiveFragment : BaseFragment<ArchiveViewModel, FragmentArchiveBinding>(),
    ArchiveNotesAdapter.ArchiveNoteListener {

    override val layoutId: Int = R.layout.fragment_archive

    override val viewModel: ArchiveViewModel by activityViewModels()

    private val archiveNotesAdapter = ArchiveNotesAdapter(this)

    private val bundle = Bundle()

    override fun initComponents() {
        initArchiveNotesRcl()
        initArchiveNotes()
        initArchiveNotesObserver()
    }

    private fun initArchiveNotesObserver() {
        viewModel.archiveNotes.observe(viewLifecycleOwner) {
            archiveNotesAdapter.submitList(it)
            binding.noItems.isGone = it.isNotEmpty()
        }
    }

    private fun initArchiveNotes() {
        viewModel.getArchiveNotes()
    }

    private fun initArchiveNotesRcl() {
        binding.notesRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = archiveNotesAdapter
        }
    }

    override fun initListeners() {

    }

    override fun onArchiveNoteClicked(note: ArchiveNote, pos: Int) {
        bundle.putSerializable("archive_note_data", note)

        val archivedNoteViewBottomSheetModal = ArchivedNoteViewBottomSheetModal()
        archivedNoteViewBottomSheetModal.arguments = bundle
        archivedNoteViewBottomSheetModal.show(
            requireFragmentManager(),
            ArchivedNoteViewBottomSheetModal::class.simpleName
        )
    }

    override fun onArchiveNoteLongClicked(note: ArchiveNote, pos: Int): Boolean {
        bundle.putSerializable("archive_note_data", note)
        val archiveNoteActionsBottomSheetModal = ArchiveNoteActionBottomSheetModal()
        archiveNoteActionsBottomSheetModal.arguments = bundle
        archiveNoteActionsBottomSheetModal.setTargetFragment(this, 1)
        archiveNoteActionsBottomSheetModal.show(requireFragmentManager(), "TAG")
        return false
    }
}
