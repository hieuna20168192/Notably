package com.example.notably.ui.reminders

import android.app.Activity
import android.content.Intent
import androidx.core.view.isGone
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.NoteAdapter
import com.example.notably.adapter.ReminderNoteAdapter
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentRemindersBinding
import com.example.notably.repos.entities.Note
import com.example.notably.ui.add.AddNoteActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemindersFragment : BaseFragment<ReminderViewModel, FragmentRemindersBinding>(),
    NoteAdapter.NotesListener {

    override val layoutId: Int = R.layout.fragment_reminders

    override val viewModel: ReminderViewModel by viewModels()

    private val reminderNotesAdapter = ReminderNoteAdapter(this)

    override fun initComponents() {
        initReminderNotesRcl()
        initNotesObserver()
        initNotes()
    }

    private fun initNotesObserver() {
        viewModel.reminderNotes.observe(viewLifecycleOwner) {
            reminderNotesAdapter.submitList(it)
            binding.noItems.isGone = it.isNotEmpty()
        }
    }

    private fun initNotes() {
        viewModel.getReminderNotes()
    }

    private fun initReminderNotesRcl() {
        binding.notesRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = reminderNotesAdapter
        }
    }

    override fun initListeners() {

    }

    override fun onNoteClicked(note: Note, pos: Int) {
        val intent = Intent(context, AddNoteActivity::class.java)
        intent.putExtra("modifier", true)
        intent.putExtra("note", note)
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK)
    }

    override fun onNoteLongClicked(note: Note, pos: Int): Boolean {
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestAddNoteSuccess(requestCode, resultCode)) {
            viewModel.getReminderNotes()
        }
    }

    private fun requestAddNoteSuccess(requestCode: Int, resultCode: Int): Boolean {
        return requestCode == REQUEST_CODE_UPDATE_NOTE_OK && resultCode == Activity.RESULT_OK
    }

    companion object {
        const val REQUEST_CODE_UPDATE_NOTE_OK = 2
    }
}
