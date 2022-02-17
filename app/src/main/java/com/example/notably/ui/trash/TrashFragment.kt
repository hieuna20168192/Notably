package com.example.notably.ui.trash

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.view.isGone
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.TrashNoteAdapter
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentTrashBinding
import com.example.notably.repos.entities.TrashNote
import com.example.notably.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrashFragment : BaseFragment<TrashViewModel, FragmentTrashBinding>(),
    TrashNoteAdapter.TrashNoteListener {

    override val layoutId: Int = R.layout.fragment_trash

    override val viewModel: TrashViewModel by activityViewModels()

    private val trashNoteAdapter = TrashNoteAdapter(this)

    private val bundle = Bundle()

    override fun initComponents() {
        initTrashNotesRcl()
        initTrashNotesObserver()
        initTrashNotes()
    }

    private fun initTrashNotes() {
        viewModel.getTrashNotes()
    }

    private fun initTrashNotesObserver() {
        viewModel.trashNotes.observe(viewLifecycleOwner) {
            trashNoteAdapter.submitList(it)
            binding.noItems.isGone = it.isNotEmpty()
            (requireActivity() as MainActivity).extraAction.isEnabled = it.isNotEmpty()
        }
    }

    private fun initTrashNotesRcl() {
        binding.notesRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = trashNoteAdapter
        }
    }

    override fun initListeners() {
        initDeleteAllTrashsListener()
    }

    private fun initDeleteAllTrashsListener() {
        (requireActivity() as MainActivity).extraAction.setOnClickListener {
            requestDeleteAll()
        }
    }

    private fun requestDeleteAll() {
        val confirmDialog = Dialog(requireContext())

        // Set style for dialog
        confirmDialog.apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.popup_confirm)
            setCancelable(true)
            setOnCancelListener { dismiss() }
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                setLayout(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT
                )
            }
        }

        val confirmHeader = confirmDialog.findViewById<TextView>(R.id.confirm_header)
        val confirmText = confirmDialog.findViewById<TextView>(R.id.confirm_text)
        val confirmAllow = confirmDialog.findViewById<TextView>(R.id.confirm_allow)
        val confirmCancel = confirmDialog.findViewById<TextView>(R.id.confirm_deny)

        confirmHeader.text = getString(R.string.delete_all_notes)
        confirmText.text = getString(R.string.delete_all_notes_description)
        confirmAllow.setOnClickListener {
            viewModel.deleteAllTrashNotes()
            confirmDialog.dismiss()
        }

        confirmCancel.setOnClickListener { confirmDialog.dismiss() }
        confirmDialog.show()
    }

    override fun onTrashNoteClicked(note: TrashNote, pos: Int) {

    }

    override fun onTrashNoteLongClicked(note: TrashNote, pos: Int): Boolean {
        bundle.putSerializable("trash_note_data", note)
        val trashNoteActionsBottomSheetModal = TrashNoteActionBottomSheetModal()
        trashNoteActionsBottomSheetModal.arguments = bundle
        trashNoteActionsBottomSheetModal.setTargetFragment(this, 3)
        trashNoteActionsBottomSheetModal.show(
            requireFragmentManager(),
            TrashNoteActionBottomSheetModal::class.simpleName
        )
        return false
    }
}
