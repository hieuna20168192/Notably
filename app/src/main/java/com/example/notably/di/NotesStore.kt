package com.example.notably.di

import com.example.notably.repos.entities.Category
import com.example.notably.repos.entities.Note
import com.example.notably.repos.entities.Task
import com.example.notably.repos.entities.TodosList

object NotesStore {

    val allNotes = listOf<Note>(
        Note(1, "Prepare for interview", "23/12/1998", "Working with smart people"),
        Note(2, "Work from home", "20/01/2022", "Working with average person"),
        Note(3, "Listen to music", "05/2/2022", "Taylor Swift author"),
        Note(4, "Meeting with customer", "12/01/2023", "Euro, Asia and sort of things, ..."),
        Note(5, "Inject vaccinated dose 3", "19/01/2022", "at Me Tri central park."),
    )

    val allCategories = listOf<Category>(
        Category(1, "Home", false),
        Category(2, "Work", true),
        Category(3, "Music", false),
        Category(4, "Study", false),
    )

    val allTasks = listOf<Task>(
        Task(1, "Task 1", "Math"),
        Task(2, "Task 2", "Physic"),
        Task(3, "Task 3", "Literature"),
        Task(4, "Task 4", "Music"),
        Task(5, "Task 5", "Encapsulation", priority = true, state = true),
    )

    val allTodosLists = listOf(
        TodosList(1, 0, "todolist1"),
        TodosList(2, 0, "todolist2"),
        TodosList(3, 0, "todolist3")
    )

    fun searchNotesByColor(keyword: String, color: String): List<Note> {
        return listOf()
    }

    fun searchNotesByImage(keyword: String): List<Note> {
        return listOf()
    }

    fun searchNotesByVideo(keyword: String): List<Note> {
        return listOf()
    }

    fun searchNotesByReminder(keyword: String): List<Note> {
        return listOf()
    }

    fun searchNotesByCategory(identifier: Int): List<Note> {
        return listOf()
    }
}
