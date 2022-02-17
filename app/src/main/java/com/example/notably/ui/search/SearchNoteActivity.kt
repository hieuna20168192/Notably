package com.example.notably.ui.search

import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notably.R
import com.example.notably.adapter.NoteAdapter
import com.example.notably.base.BaseActivity
import com.example.notably.databinding.ActivitySearchNoteBinding
import com.example.notably.repos.entities.Note
import com.example.notably.ui.add.AddNoteActivity
import com.example.notably.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SearchNoteActivity : BaseActivity<SearchViewModel, ActivitySearchNoteBinding>(),
    NoteAdapter.NotesListener {

    override val layoutId: Int = R.layout.activity_search_note

    override val viewModel: SearchViewModel by viewModels()

    private lateinit var notesAdapter: NoteAdapter

    private var selectedFilterResource: String = ""
    private var selectedFilter = 'G'
    private var isClosed = false

    private val bundle = Bundle()

    override fun initComponents() {
        initNotesAdapter()
        initNotesRcl()
        initFilterNotesObserver()
    }

    private fun initFilterNotesObserver() {
        viewModel.filterNotes.observe(this) {
            notesAdapter.submitList(it)
        }
    }

    private fun initNotesAdapter() {
        notesAdapter = NoteAdapter(this)
    }

    private fun initNotesRcl() {
        binding.notesRecyclerview.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = notesAdapter
        }
    }

    override fun initListeners() {
        initGoBackListener()
        initSearchBarListener()
        initColorSearchListener()
        initTypesSearchListener()
        initSearchMicListener()
    }

    private fun initSearchMicListener() {
        binding.searchMic.setOnClickListener {
            requestSearchMic()
        }
    }

    private fun initTypesSearchListener() {
        binding.typeImages.setOnClickListener {
            selectedFilter = 'T'
            selectedFilterResource = "_images"
            requestSetSearchAction()
        }

        binding.typeVideos.setOnClickListener {
            selectedFilter = 'T'
            selectedFilterResource = "_videos"
            requestSetSearchAction()
        }

        binding.typeReminders.setOnClickListener {
            selectedFilter = 'T'
            selectedFilterResource = "_reminders"
            requestSetSearchAction()
        }
    }

    private fun initColorSearchListener() {
        binding.noteThemeOne.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#fffee7ab"
            requestSetSearchAction()
        }

        binding.noteThemeTwo.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffffdbc3"
            requestSetSearchAction()
        }

        binding.noteThemeThree.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffffc5d1"
            requestSetSearchAction()
        }

        binding.noteThemeFour.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffe7d0f9"
            requestSetSearchAction()
        }

        binding.noteThemeFive.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffcdccfe"
            requestSetSearchAction()
        }

        binding.noteThemeSix.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffb5e9d3"
            requestSetSearchAction()
        }

        binding.noteThemeSeven.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffb3e5fd"
            requestSetSearchAction()
        }

        binding.noteThemeEight.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffb5d8ff"
            requestSetSearchAction()
        }

        binding.noteThemeNine.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffe5e5e5"
            requestSetSearchAction()
        }

        binding.noteThemeTen.setOnClickListener {
            selectedFilter = 'C'
            selectedFilterResource = "#ffbcbcbc"
            requestSetSearchAction()
        }
    }

    private fun initSearchBarListener() {
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                if (count == 0) {
                    requestSetSearchAction()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (!isClosed) {
                    searchNotes(s.toString())
                }
            }
        })
    }

    private fun searchNotes(keyword: String) {
        when (selectedFilter) {
            'C' -> {
                viewModel.searchNotesByColor(keyword, selectedFilterResource)
            }
            'T' -> {
                when (selectedFilterResource) {
                    "_images" -> {
                        viewModel.searchNotesByImage(keyword)
                    }
                    "_videos" -> {
                        viewModel.searchNotesByVideo(keyword)
                    }
                    "_reminders" -> {
                        viewModel.searchNotesByReminder(keyword)
                    }
                }
            }
            'G' -> {
                viewModel.searchNotesByGlobal(keyword)
            }
        }
    }

    private fun requestSetSearchAction() {
        isClosed = false

        // hide search content container
        binding.searchContentContainer.visibility = View.GONE

        // hide back arrow and show close icon
        binding.goBack.visibility = View.GONE
        binding.closeSearch.visibility = View.VISIBLE

        // request focus for search bar and show the keyboard
        binding.searchBar.requestFocus()
        showKeyBoard()

        // initialize action for close search
        binding.closeSearch.setOnClickListener {
            requestCloseSearch()
        }
    }

    private fun requestCloseSearch() {
        // hide keyboard
        hideKeyBoard()

        // hide close icon and show back arrow and search content container
        binding.closeSearch.visibility = View.GONE
        binding.goBack.visibility = View.VISIBLE
        binding.searchContentContainer.visibility = View.VISIBLE

        // set selected filter for global search
        selectedFilterResource = ""
        selectedFilter = 'G'

        // clear search bar text
        isClosed = true
        binding.searchBar.text.clear()
    }

    private fun initGoBackListener() {
        binding.goBack.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_TEXT_TO_SPEECH) {
            if (resultCode == RESULT_OK && null != data) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                result?.let {
                    binding.searchBar.setText(it[0])
                }
            }
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE_OK && resultCode == RESULT_OK) {
            searchNotes(binding.searchBar.text.toString())
        }
    }

    override fun onNoteClicked(note: Note, pos: Int) {
        val intent = Intent(this, AddNoteActivity::class.java)
        intent.apply {
            putExtra("modifier", true)
            putExtra("note", note)
            startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE_OK)
        }
    }

    override fun onNoteLongClicked(note: Note, pos: Int): Boolean {
        return false
    }

    override fun onBackPressed() {
        if (binding.closeSearch.visibility == View.VISIBLE) {
            requestCloseSearch()
        } else {
            finish()
        }
    }

    companion object {
        const val REQUEST_CODE_TEXT_TO_SPEECH = 4
        const val REQUEST_CODE_UPDATE_NOTE_OK = 2
    }
}
