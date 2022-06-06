package com.example.notably.repos.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.notably.repos.dao.NoteDao
import com.example.notably.repos.entities.*

@Database(
    entities = [Note::class, Category::class, Task::class, TodosList::class, TrashNote::class, ArchiveNote::class, Notification::class],
    version = 1,
    exportSchema = false
)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}
