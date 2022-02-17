package com.example.notably.filter

import androidx.lifecycle.*
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.Note
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FilterNotesVM @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteRepos: NoteDataSource

    private val _notesType: MutableLiveData<List<Note>> = MutableLiveData()

    private val _key: MutableLiveData<String> = MutableLiveData("")

    val filterNotesType: LiveData<List<Note>> = _key.switchMap { key ->
        if (key.isEmpty()) {
            _notesType
        } else {
            _notesType.map { allNotes ->
                allNotes.filter {
                    it.title.contains(key, ignoreCase = true)
                            || it.subtitle.contains(key, ignoreCase = true)
                            || it.description.contains(key, ignoreCase = true)
                }
            }
        }
    }

    fun getNotesByType(identifier: Int) = runIO {
        _notesType.postValue(noteRepos.searchNotesByCategory(identifier))
        _key.postValue(_key.value)
    }

    fun filterNotesByKey(key: String) {
        _key.postValue(key)
    }
}
