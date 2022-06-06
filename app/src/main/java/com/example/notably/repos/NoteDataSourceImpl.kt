package com.example.notably.repos

import com.example.notably.di.NotesStore
import com.example.notably.repos.dao.NoteDao
import com.example.notably.repos.entities.*
import kotlinx.coroutines.delay
import javax.inject.Inject

class NoteDataSourceImpl @Inject constructor(
    private val noteDao: NoteDao
) : NoteDataSource {

    override suspend fun getNotes(sortBy: String): List<Note> {
        return noteDao.getNotes(sortBy)
    }

    override suspend fun saveNote(note: Note) {
        noteDao.saveNote(note)
    }

    override suspend fun deleteNote(note: Note) {
        noteDao.deleteNote(note)
    }

    override suspend fun getCategories(): List<Category> {
        return noteDao.getCategories()
    }

    override suspend fun saveCategory(category: Category) {
        return noteDao.insertCategory(category)
    }

    override suspend fun deleteCategory(category: Category) {
        noteDao.deleteCategory(category)
    }

    override suspend fun searchNotesByGlobal(keyword: String): List<Note> {
        return noteDao.searchNotesByGlobal(keyword)
    }

    override suspend fun searchNotesByColor(keyword: String, color: String): List<Note> {
        return noteDao.searchNotesByColor(keyword, color)
    }

    override suspend fun searchNotesByImage(keyword: String): List<Note> {
        return noteDao.searchNotesByImage(keyword)
    }

    override suspend fun searchNotesByVideo(keyword: String): List<Note> {
        return noteDao.searchNotesByVideo(keyword)
    }

    override suspend fun searchNotesByReminder(keyword: String): List<Note> {
        return noteDao.searchNotesByReminder(keyword)
    }

    override suspend fun searchNotesByCategory(identifier: Int): List<Note> {
        return noteDao.searchNotesByCategory(identifier)
    }

    override suspend fun getTasks(sortBy: String): List<Task> {
        return noteDao.getTasks(sortBy)
    }

    override suspend fun saveTask(task: Task) {
        return noteDao.saveTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        return noteDao.deleteTask(task)
    }

    override suspend fun markTask(id: Int, state: Int) {
        return noteDao.markTask(id, state)
    }

    override suspend fun getTodosLists(): List<TodosList> {
        return noteDao.getTodosLists()
    }

    override suspend fun saveTodoList(todosList: TodosList) {
        return noteDao.saveTodoList(todosList)
    }

    override suspend fun getTasksByList(listId: Int): List<Task> {
        return noteDao.getTasksByList(listId)
    }

    override suspend fun getNumOfCompletedTasks(): Int {
        return noteDao.getNumOfCompletedTasks()
    }

    override suspend fun deleteAllCompletedTasks() {
        return noteDao.deleteAllCompletedTasks()
    }

    override suspend fun getReminderNotes(): List<Note> {
        return noteDao.getReminderNotes()
    }

    override suspend fun insertTrashNote(trashNote: TrashNote) {
        noteDao.insertTrashNote(trashNote)
    }

    override suspend fun deleteTrashNote(trashNote: TrashNote) {
        noteDao.deleteTrashNote(trashNote)
    }

    override suspend fun deleteAllTrashNotes() {
        noteDao.deleteAllTrashNotes()
    }

    override suspend fun getTrashNotes(): List<TrashNote> {
        return noteDao.getTrashNotes()
    }

    override suspend fun insertArchiveNote(archiveNote: ArchiveNote) {
        noteDao.insertArchiveNote(archiveNote)
    }

    override suspend fun getArchiveNotes(): List<ArchiveNote> {
        return noteDao.getArchiveNotes()
    }

    override suspend fun deleteArchiveNotes(archiveNote: ArchiveNote) {
        noteDao.deleteArchiveNotes(archiveNote)
    }

    override suspend fun insertNotification(notification: Notification) {
        noteDao.insertNotification(notification)
    }

    override suspend fun getNotifications(): List<Notification> {
        return noteDao.getNotifications()
    }

    override suspend fun deleteAllNotifications() {
        noteDao.deleteAllNotifications()
    }
}

class FakeDataSourceImpl @Inject constructor() : NoteDataSource {
    override suspend fun getNotes(sortBy: String): List<Note> {
        delay(200L)
        return NotesStore.allNotes
    }

    override suspend fun saveNote(note: Note) {
        delay(200L)
    }

    override suspend fun deleteNote(note: Note) {
        delay(200L)
    }

    override suspend fun getCategories(): List<Category> {
        return NotesStore.allCategories
    }

    override suspend fun saveCategory(category: Category) {
        delay(200L)
    }

    override suspend fun deleteCategory(category: Category) {
        delay(200L)
    }

    override suspend fun searchNotesByGlobal(keyword: String): List<Note> {
        return NotesStore.allNotes.filter {
            it.title.contains(keyword) || it.description.contains(
                keyword
            )
        }
    }

    override suspend fun searchNotesByColor(keyword: String, color: String): List<Note> {
        return NotesStore.searchNotesByColor(keyword, color)
    }

    override suspend fun searchNotesByImage(keyword: String): List<Note> {
        return NotesStore.searchNotesByImage(keyword)
    }

    override suspend fun searchNotesByVideo(keyword: String): List<Note> {
        return NotesStore.searchNotesByVideo(keyword)
    }

    override suspend fun searchNotesByReminder(keyword: String): List<Note> {
        return NotesStore.searchNotesByReminder(keyword)
    }

    override suspend fun searchNotesByCategory(identifier: Int): List<Note> {
        return NotesStore.searchNotesByCategory(identifier)
    }

    override suspend fun getTasks(sortBy: String): List<Task> {
        return NotesStore.allTasks
    }

    override suspend fun saveTask(task: Task) {
        delay(200L)
    }

    override suspend fun deleteTask(task: Task) {
        delay(200L)
    }

    override suspend fun markTask(id: Int, state: Int) {
        delay(200L)
    }

    override suspend fun getTodosLists(): List<TodosList> {
        return NotesStore.allTodosLists
    }

    override suspend fun saveTodoList(todosList: TodosList) {
        delay(200L)
    }

    override suspend fun getTasksByList(listId: Int): List<Task> {
        return emptyList()
    }

    override suspend fun getNumOfCompletedTasks(): Int {
        return NotesStore.allTasks.size
    }

    override suspend fun deleteAllCompletedTasks() {
        delay(200L)
    }

    override suspend fun getReminderNotes(): List<Note> {
        return emptyList()
    }

    override suspend fun insertTrashNote(trashNote: TrashNote) {
        delay(200L)
    }

    override suspend fun deleteTrashNote(trashNote: TrashNote) {
        delay(200L)
    }

    override suspend fun deleteAllTrashNotes() {
        delay(200L)
    }

    override suspend fun getTrashNotes(): List<TrashNote> {
        return emptyList()
    }

    override suspend fun insertArchiveNote(archiveNote: ArchiveNote) {
        delay(200L)
    }

    override suspend fun getArchiveNotes(): List<ArchiveNote> {
        return emptyList()
    }

    override suspend fun deleteArchiveNotes(archiveNote: ArchiveNote) {
        delay(200L)
    }

    override suspend fun insertNotification(notification: Notification) {
        delay(200L)
    }

    override suspend fun getNotifications(): List<Notification> {
        return emptyList()
    }

    override suspend fun deleteAllNotifications() {
        delay(200L)
    }
}
