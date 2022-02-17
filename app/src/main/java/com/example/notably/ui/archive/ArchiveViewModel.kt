package com.example.notably.ui.archive

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.ArchiveNote
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArchiveViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteRepos: NoteDataSource

    private val _archiveNotes: MutableLiveData<List<ArchiveNote>> = MutableLiveData()
    val archiveNotes: LiveData<List<ArchiveNote>> = _archiveNotes

    fun getArchiveNotes() = runIO {
        _archiveNotes.postValue(noteRepos.getArchiveNotes())
    }

    fun deleteAndRefreshArchiveNote(archiveNote: ArchiveNote) = runIO {
        noteRepos.deleteArchiveNotes(archiveNote)
        _archiveNotes.postValue(noteRepos.getArchiveNotes())
    }
}
