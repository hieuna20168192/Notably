package com.example.notably.filter

import android.content.Intent
import android.speech.RecognizerIntent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notably.R
import com.example.notably.adapter.NoteAdapter
import com.example.notably.adapter.NotesTypeListAdapter
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivityFilteredNotesBinding
import com.example.notably.extensions.onTextChange
import com.example.notably.repos.entities.Note
import com.example.notably.ui.add.AddNoteActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*

@AndroidEntryPoint
class FilteredNotesActivity : BaseActivity<FilterNotesVM, ActivityFilteredNotesBinding>(),
    NoteAdapter.NotesListener {

    override val layoutId: Int = R.layout.activity_filtered_notes

    override val viewModel: FilterNotesVM by viewModels()

    private val notesTypeListAdapter: NotesTypeListAdapter = NotesTypeListAdapter(this)

    override fun initComponents() {
        initCategoryTitle()
        initNotesTypeRcl()
        initNotesTypeObserver()
        initNotesType()
    }

    private fun initNotesTypeObserver() {
        viewModel.filterNotesType.observe(this) {
            if (it.isEmpty()) {
                binding.noItems.visibility = View.VISIBLE
            } else {
                binding.noItems.visibility = View.GONE
            }
            notesTypeListAdapter.submitList(it)
        }
    }

    private fun initNotesType() {
        viewModel.getNotesByType(intent.getIntExtra("identifier", 0))
    }

    private fun initNotesTypeRcl() {
        binding.notesRecyclerview.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.notesRecyclerview.adapter = notesTypeListAdapter
    }

    private fun initCategoryTitle() {
        binding.categoryTitle.text = intent.getStringExtra("title")
    }

    override fun initListeners() {
        initGoBackListener()
        initSearchBarListener()
        initSearchMicListener()
    }

    private fun initSearchMicListener() {
        binding.searchMic.setOnClickListener {
            requestSearchMic()
        }
    }

    private fun initSearchBarListener() {
        binding.searchBar.onTextChange()
            .debounce(500L)
            .distinctUntilChanged()
            .onEach { key ->
                viewModel.filterNotesByKey(key)
            }
            .launchIn(viewModel.viewModelScope)
    }

    private fun initGoBackListener() {
        binding.goBack.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TEXT_TO_SPEECH) {
            if (resultCode == RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                if (result != null) {
                    binding.searchBar.setText(result[0])
                }
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE_OK) {
            viewModel.getNotesByType(intent.getIntExtra("identifier", 0))
        }
    }

    override fun onNoteClicked(note: Note, pos: Int) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.apply {
            putExtra("modifier", true)
            putExtra("note", note)
        }
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK)
    }

    override fun onNoteLongClicked(note: Note, pos: Int): Boolean {
        return false
    }

    companion object {
        const val REQUEST_CODE_TEXT_TO_SPEECH = 4
        const val REQUEST_CODE_UPDATE_NOTE_OK = 2
    }
}
