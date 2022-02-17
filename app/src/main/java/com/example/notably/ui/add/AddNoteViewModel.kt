package com.example.notably.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.ArchiveNote
import com.example.notably.repos.entities.Note
import com.example.notably.repos.entities.TrashNote
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteRepos: NoteDataSource

    fun saveNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {
        noteRepos.saveNote(note)
    }

    fun insertToArchive(note: Note) = runIO {
        val archiveNote = ArchiveNote(
            note.noteId,
            note.title,
            note.createdAt,
            note.subtitle,
            note.description,
            note.imagePath,
            note.color,
            note.webLink,
            note.categoryId,
            note.reminder,
            note.imageUri,
            note.videoPath
        )
        noteRepos.insertArchiveNote(archiveNote)
    }

    fun insertTrashNote(note: Note) = runIO {
        val copyNote = TrashNote(
            note.noteId,
            note.title,
            note.createdAt,
            note.subtitle,
            note.description,
            note.imagePath,
            note.color,
            note.webLink,
            note.categoryId,
            note.reminder
        )
        noteRepos.insertTrashNote(copyNote)
        noteRepos.deleteNote(note)
    }
}
