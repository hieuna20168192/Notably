package com.example.notably.ui.reminders

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
class ReminderViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteRepos: NoteDataSource

    private val _reminderNotes: MutableLiveData<List<Note>> = MutableLiveData()
    val reminderNotes: LiveData<List<Note>> = _reminderNotes

    fun getReminderNotes() = runIO {
        _reminderNotes.postValue(noteRepos.getReminderNotes())
    }
}
