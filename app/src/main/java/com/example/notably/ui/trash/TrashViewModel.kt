package com.example.notably.ui.trash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.Note
import com.example.notably.repos.entities.TrashNote
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteRepos: NoteDataSource

    private val _trashNotes: MutableLiveData<List<TrashNote>> = MutableLiveData()
    val trashNotes: LiveData<List<TrashNote>> = _trashNotes

    fun getTrashNotes() = runIO {
        _trashNotes.postValue(noteRepos.getTrashNotes())
    }

    fun deleteAllTrashNotes() = runIO {
        noteRepos.deleteAllTrashNotes()
        _trashNotes.postValue(noteRepos.getTrashNotes())
    }

    fun restoreNote(trashNote: TrashNote) = runIO {
        val note = Note(
            trashNote.noteId,
            trashNote.title,
            trashNote.createdAt,
            trashNote.subtitle,
            trashNote.description,
            trashNote.imagePath,
            "",
            "",
            trashNote.color,
            trashNote.webLink,
            trashNote.categoryId,
            trashNote.reminder,
        )
        noteRepos.saveNote(note)
        noteRepos.deleteTrashNote(trashNote)
        _trashNotes.postValue(noteRepos.getTrashNotes())
    }

    fun deleteTrashNote(trashNote: TrashNote) = runIO {
        noteRepos.deleteTrashNote(trashNote)
        _trashNotes.postValue(noteRepos.getTrashNotes())
    }
}
