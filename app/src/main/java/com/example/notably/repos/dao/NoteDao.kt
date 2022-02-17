package com.example.notably.repos.dao

import androidx.room.*
import com.example.notably.repos.entities.*

@Dao
interface NoteDao {

    @Query(
        "SELECT * FROM notes ORDER BY CASE WHEN :sortBy = 'note_id' THEN note_id END DESC, " +
                "CASE WHEN :sortBy = 'a_z' THEN note_title END ASC, " +
                "CASE WHEN :sortBy = 'z_a' THEN note_title END DESC"
    )
    suspend fun getNotes(sortBy: String): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Query("SELECT * FROM categories ORDER BY categoryId DESC")
    suspend fun getCategories(): List<Category>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query(
        "SELECT * FROM notes WHERE note_title LIKE '%' || :keyword || '%'" +
                "OR note_description LIKE '%' || :keyword || '%' ORDER BY note_id DESC"
    )
    suspend fun searchNotesByGlobal(keyword: String): List<Note>

    @Query(
        "SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
                "OR note_description LIKE '%' || :keyword || '%') AND note_color = :color ORDER BY note_id DESC"
    )
    suspend fun searchNotesByColor(keyword: String, color: String): List<Note>

    @Query(
        "SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
                "OR note_description LIKE '%' || :keyword || '%') AND note_image_path != '' ORDER BY note_id DESC"
    )
    suspend fun searchNotesByImage(keyword: String): List<Note>

    @Query(
        "SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
                "OR note_description LIKE '%' || :keyword || '%') AND note_video_path != '' ORDER BY note_id DESC"
    )
    suspend fun searchNotesByVideo(keyword: String): List<Note>

    @Query(
        "SELECT * FROM notes WHERE (note_title LIKE '%' || :keyword || '%'" +
                "OR note_description LIKE '%' || :keyword || '%') AND note_reminder != '' ORDER BY note_id DESC"
    )
    suspend fun searchNotesByReminder(keyword: String): List<Note>

    @Query("SELECT * FROM notes WHERE note_category_id = :identifier ORDER BY note_id DESC")
    suspend fun searchNotesByCategory(identifier: Int): List<Note>

    @Query("SELECT * FROM tasks ORDER BY CASE WHEN :sortBy = 'taskId' THEN taskId END DESC, CASE WHEN :sortBy = 'a_z' THEN task_title END ASC, CASE WHEN :sortBy = 'z_a' THEN task_title END DESC")
    suspend fun getTasks(sortBy: String): List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Query("UPDATE tasks SET task_state = :state WHERE taskId = :id")
    suspend fun markTask(id: Int, state: Int)

    @Query("SELECT * FROM todo_lists ORDER BY todoListId DESC")
    suspend fun getTodosLists(): List<TodosList>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveTodoList(todosList: TodosList)

    @Query("SELECT * FROM tasks WHERE task_list = :listId ORDER BY taskId DESC")
    suspend fun getTasksByList(listId: Int): List<Task>

    @Query("SELECT COUNT(taskId) FROM tasks WHERE task_state = 1")
    suspend fun getNumOfCompletedTasks(): Int

    @Query("DELETE FROM tasks WHERE task_state = 1")
    suspend fun deleteAllCompletedTasks()

    @Query("SELECT * FROM notes WHERE note_reminder != '' ORDER BY note_id DESC")
    suspend fun getReminderNotes(): List<Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrashNote(trashNote: TrashNote)

    @Delete
    suspend fun deleteTrashNote(trashNote: TrashNote)

    @Query("DELETE FROM trash_notes")
    suspend fun deleteAllTrashNotes()

    @Query("SELECT * FROM trash_notes ORDER BY noteId DESC")
    suspend fun getTrashNotes(): List<TrashNote>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArchiveNote(archiveNote: ArchiveNote)

    @Query("SELECT * FROM archive_notes ORDER BY noteId DESC")
    suspend fun getArchiveNotes(): List<ArchiveNote>

    @Delete
    suspend fun deleteArchiveNotes(archiveNote: ArchiveNote)
}
