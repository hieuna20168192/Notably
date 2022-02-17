package com.example.notably.ui.sheets.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notably.di.RealRepos
import com.example.notably.extensions.runIO
import com.example.notably.repos.NoteDataSource
import com.example.notably.repos.entities.Category
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor() : ViewModel() {

    @Inject
    @RealRepos
    lateinit var noteDataSource: NoteDataSource

    private val _categories: MutableLiveData<List<Category>> = MutableLiveData()
    val categories: LiveData<List<Category>> = _categories

    fun getCategories() = viewModelScope.launch(Dispatchers.IO) {
        _categories.postValue(noteDataSource.getCategories())
    }

    fun saveCategory(category: Category) = viewModelScope.launch(Dispatchers.IO) {
        noteDataSource.saveCategory(category)
    }

    fun saveAndRefreshCategories(category: Category) = viewModelScope.launch(Dispatchers.IO){
        noteDataSource.saveCategory(category)
        _categories.postValue(noteDataSource.getCategories())
    }

    fun deleteCategory(category: Category) = runIO {
        noteDataSource.deleteCategory(category)
    }

    fun deleteAndRefreshCategories(category: Category) = runIO {
        noteDataSource.deleteCategory(category)
        _categories.postValue(noteDataSource.getCategories())
    }
}
