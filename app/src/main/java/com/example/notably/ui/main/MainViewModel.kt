package com.example.notably.ui.main

import androidx.lifecycle.ViewModel
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteDataSource: NoteDataSource

    fun saveNotification(notification: Notification) = runIO {
        noteDataSource.insertNotification(notification)
    }
}
