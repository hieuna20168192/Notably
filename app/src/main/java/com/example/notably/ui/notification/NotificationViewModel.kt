package com.example.notably.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    @Inject
    @RealRepos
    lateinit var noteDataSource: NoteDataSource

    private val _notifications: MutableLiveData<List<Notification>> = MutableLiveData()
    val notifications: LiveData<List<Notification>> = _notifications

    fun getNotifications() = runIO {
        delay(200L)
        _notifications.postValue(noteDataSource.getNotifications())
    }

    fun saveNotification(notification: Notification) = runIO {
        noteDataSource.insertNotification(notification)
        _notifications.postValue(noteDataSource.getNotifications())
    }

    fun deleteAllNotifications() = runIO {
        noteDataSource.deleteAllNotifications()
        _notifications.postValue(noteDataSource.getNotifications())
    }
}
