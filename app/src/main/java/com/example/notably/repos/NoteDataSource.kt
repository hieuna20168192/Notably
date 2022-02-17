package com.example.notably.repos

import com.example.notably.repos.entities.*

interface NoteDataSource {
    suspend fun getNotes(sortBy: String): List<Note>
    suspend fun saveNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun getCategories(): List<Category>
    suspend fun saveCategory(category: Category)
    suspend fun deleteCategory(category: Category)
    suspend fun searchNotesByGlobal(keyword: String): List<Note>
    suspend fun searchNotesByColor(keyword: String, color: String): List<Note>
    suspend fun searchNotesByImage(keyword: String): List<Note>
    suspend fun searchNotesByVideo(keyword: String): List<Note>
    suspend fun searchNotesByReminder(keyword: String): List<Note>
    suspend fun searchNotesByCategory(identifier: Int): List<Note>
    suspend fun getTasks(sortBy: String): List<Task>
    suspend fun saveTask(task: Task)
    suspend fun deleteTask(task: Task)
    suspend fun markTask(id: Int, state: Int)
    suspend fun getTodosLists(): List<TodosList>
    suspend fun saveTodoList(todosList: TodosList)
    suspend fun getTasksByList(listId: Int): List<Task>
    suspend fun getNumOfCompletedTasks(): Int
    suspend fun deleteAllCompletedTasks()
    suspend fun getReminderNotes(): List<Note>
    suspend fun insertTrashNote(trashNote: TrashNote)
    suspend fun deleteTrashNote(trashNote: TrashNote)
    suspend fun deleteAllTrashNotes()
    suspend fun getTrashNotes(): List<TrashNote>
    suspend fun insertArchiveNote(archiveNote: ArchiveNote)
    suspend fun getArchiveNotes(): List<ArchiveNote>
    suspend fun deleteArchiveNotes(archiveNote: ArchiveNote)
}
