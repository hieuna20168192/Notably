package com.example.notably.ui.home.note

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.notably.R
import com.example.notably.adapter.ChipCategoryAdapter
import com.example.notably.adapter.NoteAdapter
import com.example.notably.base.BaseFragment
import com.example.notably.databinding.FragmentNoteBinding
import com.example.notably.filter.FilteredNotesActivity
import com.example.notably.repos.entities.Category
import com.example.notably.repos.entities.Note
import com.example.notably.ui.add.AddNoteActivity
import com.example.notably.ui.edit.category.EditCategoryActivity
import com.example.notably.ui.home.HomeViewModel
import com.example.notably.ui.main.MainActivity
import com.example.notably.ui.search.SearchNoteActivity
import com.example.notably.ui.sheets.HomeMoreOptionsBottomSheetModal
import com.example.notably.ui.sheets.NoteActionsBottomSheetModal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteFragment : BaseFragment<HomeViewModel, FragmentNoteBinding>(),
    NoteAdapter.NotesListener,
    ChipCategoryAdapter.ChipCategoryListener {

    override val layoutId: Int = R.layout.fragment_note

    override val viewModel: HomeViewModel by viewModels()
    private val bundle = Bundle()

    private val notesAdapter: NoteAdapter = NoteAdapter(this)
    private val chipCategoriesAdapter: ChipCategoryAdapter = ChipCategoryAdapter(this)

    override fun initComponents() {
        initNotesRcl()
        initNotesObserver()
        initNotes()
        initCategoriesRcl()
        initCategories()
        initCategoriesObserver()
    }

    private fun initCategoriesObserver() {
        viewModel.categories.observe(viewLifecycleOwner) {
            chipCategoriesAdapter.submitList(it)
        }
    }

    private fun initCategories() {
        viewModel.getCategories()
    }

    private fun initCategoriesRcl() {
        binding.categoriesRecyclerview.apply {
            layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = chipCategoriesAdapter
        }
    }

    private fun initNotesRcl() {
        binding.notesRecyclerview.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = notesAdapter
        }
    }

    private fun initNotesObserver() {
        viewModel.notes.observe(viewLifecycleOwner) { notes ->
            notesAdapter.submitList(notes)
        }
    }

    private fun initNotes() {
        viewModel.getNotes()
    }

    override fun initListeners() {
        initAddNoteFabListener()
        initMoreOptionsListener()
        initSearchNoteListener()
        initAddCategoryListener()
    }

    private fun initAddCategoryListener() {
        binding.addCategory.setOnClickListener {
            startActivity(Intent(context, EditCategoryActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun initSearchNoteListener() {
        binding.searchBar.setOnClickListener {
            startActivity(Intent(context, SearchNoteActivity::class.java))
        }
    }

    private fun initMoreOptionsListener() {
        (requireActivity() as MainActivity).moreOptions.setOnClickListener {
            val homeMoreOptionsBottomSheetModal = HomeMoreOptionsBottomSheetModal()
            homeMoreOptionsBottomSheetModal.setTargetFragment(this, 1)
            homeMoreOptionsBottomSheetModal.show(
                requireFragmentManager(),
                HomeMoreOptionsBottomSheetModal::class.simpleName
            )
        }
    }

    private fun initAddNoteFabListener() {
        binding.addNote.setOnClickListener {
            startActivityForResult(
                Intent(requireContext(), AddNoteActivity::class.java),
                REQUEST_CODE_ADD_NOTE_OK
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d("requestCode", "$requestCode")
        Log.d("resultCode", "$resultCode")
        super.onActivityResult(requestCode, resultCode, data)
        when {
            requestCode == REQUEST_CODE_ADD_NOTE_OK && resultCode == RESULT_OK -> {
                viewModel.getNotes()
            }
            requestCode == REQUEST_MOVE_TO_TRASH && resultCode == NoteActionsBottomSheetModal.REQUEST_DELETE_NOTE_CODE -> {
                viewModel.getNotes()
            }
            resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_OPTION_REQUEST_CODE -> {
                initializeToolbarSelector()
            }
            resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_A_TO_Z -> {
                viewModel.getNotes("a_z")
            }
            resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_Z_TO_A -> {
                viewModel.getNotes("z_a")
            }
            resultCode == HomeMoreOptionsBottomSheetModal.CHOOSE_SORT_BY_DEFAULT -> {
                viewModel.getNotes("note_id")
            }
        }
    }

    private fun initializeToolbarSelector() {
        (requireActivity() as MainActivity).apply {
            toolbarSelector.visibility = View.VISIBLE

            // close toolbar selector
            toolbarSelectorClose.setOnClickListener {
                toolbarSelectorSelectedItems.text = "0 ${getString(R.string.selected)}"

                // clear selections
                notesAdapter.clearSelection()
                toolbarSelector.visibility = View.GONE
            }

            // request delete selected notes
            (requireActivity() as MainActivity).toolbarSelectorDeleteNotes.setOnClickListener {
                Toast.makeText(context, "This feature is under development.", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onNoteClicked(note: Note, pos: Int) {
        if (hasToggleSelector()) {
            requestToggleSelection(pos)
        } else {
            routeEditScreen(note)
        }
    }

    private fun requestToggleSelection(pos: Int) {
        notesAdapter.toggleSelection(pos)
        (requireActivity() as MainActivity).toolbarSelectorSelectedItems.text =
            ("${notesAdapter.getSelectedItemCount()} ${getString(R.string.selected)}")
    }

    private fun hasToggleSelector(): Boolean {
        return (requireActivity() as MainActivity).toolbarSelector.visibility == View.VISIBLE
    }

    private fun routeEditScreen(note: Note) {
        val intent = Intent(requireContext(), AddNoteActivity::class.java)
        intent.putExtra("modifier", true)
        intent.putExtra("note", note)
        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE_OK)
    }

    override fun onNoteLongClicked(note: Note, pos: Int): Boolean {
        bundle.putSerializable("note_data", note)
        val noteActionsBottomSheetModal = NoteActionsBottomSheetModal()
        noteActionsBottomSheetModal.arguments = bundle
        noteActionsBottomSheetModal.setTargetFragment(this, REQUEST_MOVE_TO_TRASH)
        noteActionsBottomSheetModal.show(
            requireFragmentManager(),
            NoteActionsBottomSheetModal::class.simpleName
        )
        return false
    }

    override fun onCategoryClicked(category: Category, position: Int) {
        val intent = Intent(context, FilteredNotesActivity::class.java)
        intent.putExtra("identifier", category.categoryId)
        intent.putExtra("title", category.title)
        startActivity(intent)
    }

    companion object {
        const val REQUEST_CODE_ADD_NOTE_OK = 1
        const val REQUEST_MOVE_TO_TRASH = 2
    }
}
