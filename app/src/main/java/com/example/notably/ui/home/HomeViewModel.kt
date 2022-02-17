package com.example.notably.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteRepos: NoteDataSource

    private val _notes: MutableLiveData<List<Note>> = MutableLiveData()
    val notes: LiveData<List<Note>> = _notes

    private val _categories: MutableLiveData<List<Category>> = MutableLiveData()
    val categories: LiveData<List<Category>> = _categories

    private val _tasks: MutableLiveData<List<Task>> = MutableLiveData()
    val tasks: LiveData<List<Task>> = _tasks

    private val _todosLists: MutableLiveData<List<TodosList>> = MutableLiveData()
    val todosLists: LiveData<List<TodosList>> = _todosLists

    private val _numOfCompletedTasks: MutableLiveData<Int> = MutableLiveData()
    val numOfCompletedTasks: LiveData<Int> = _numOfCompletedTasks

    fun getNotes(sortBy: String = "note_id") = viewModelScope.launch(Dispatchers.IO) {
        delay(200L)
        val result = noteRepos.getNotes(sortBy)
        _notes.postValue(result)
    }

    fun getCategories() = runIO {
        _categories.postValue(noteRepos.getCategories())
    }

    fun getTasks(sortBy: String = "taskId") = runIO {
        _tasks.postValue(noteRepos.getTasks(sortBy))
    }

    fun saveTask(task: Task) = runIO {
        noteRepos.saveTask(task)
    }

    fun saveAndRefresh(task: Task) = runIO {
        noteRepos.saveTask(task)
        _tasks.postValue(noteRepos.getTasks(sortBy = "taskId"))
    }

    fun saveAndRefreshByList(task: Task, listId: Int) = runIO {
        noteRepos.saveTask(task)
        _tasks.postValue(noteRepos.getTasksByList(listId))
    }

    fun deleteTask(task: Task) = runIO {
        noteRepos.deleteTask(task)
    }

    fun markTask(id: Int, state: Int) = runIO {
        noteRepos.markTask(id, state)
    }

    fun getTodosLists() = runIO {
        _todosLists.postValue(noteRepos.getTodosLists())
    }

    fun saveTodosList(todosList: TodosList) = runIO {
        noteRepos.saveTodoList(todosList)
    }

    fun getTasksByList(listId: Int) = runIO {
        _tasks.postValue(noteRepos.getTasksByList(listId))
    }

    fun getNumOfCompletedTasks() = runIO {
        _numOfCompletedTasks.postValue(noteRepos.getNumOfCompletedTasks())
    }

    fun deleteAllCompletedTasksAndRefresh() = runIO {
        noteRepos.deleteAllCompletedTasks()
        _tasks.postValue(noteRepos.getTasks("taskId"))
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
