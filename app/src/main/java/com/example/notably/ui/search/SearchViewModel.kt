package com.example.notably.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteDataSource: NoteDataSource

    private val _filterNotes: MutableLiveData<List<Note>> = MutableLiveData()
    val filterNotes: LiveData<List<Note>> = _filterNotes

    fun searchNotesByGlobal(keyword: String) = runIO {
        _filterNotes.postValue(noteDataSource.searchNotesByGlobal(keyword))
    }

    fun searchNotesByColor(keyword: String, filterRes: String) = runIO {
        _filterNotes.postValue(noteDataSource.searchNotesByColor(keyword, filterRes))
    }

    fun searchNotesByImage(keyword: String) = runIO {
        _filterNotes.postValue(noteDataSource.searchNotesByImage(keyword))
    }

    fun searchNotesByVideo(keyword: String) = runIO {
        _filterNotes.postValue(noteDataSource.searchNotesByVideo(keyword))
    }

    fun searchNotesByReminder(keyword: String) = runIO {
        _filterNotes.postValue(noteDataSource.searchNotesByReminder(keyword))
    }
}
