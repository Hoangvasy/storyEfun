package com.example.storyefun.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.storyefun.data.models.Category
import com.example.storyefun.data.repository.CategoryRepository

class CategoryViewModel : ViewModel() {
    private val repository = CategoryRepository()

    // Sử dụng mutableStateOf để theo dõi danh sách category được chọn
    val selectedCategories = mutableStateOf<List<String>>(emptyList())

    // LiveData để theo dõi danh sách category
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    // LiveData để theo dõi trạng thái loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun fetchCategories() {
        _isLoading.value = true
        repository.getCategories { result ->
            _categories.value = result
            _isLoading.value = false
        }
    }

    fun toggleCategorySelection(categoryId: String) {
        val currentList = selectedCategories.value
        if (currentList.contains(categoryId)) {
            selectedCategories.value = currentList - categoryId
        } else {
            selectedCategories.value = currentList + categoryId
        }
    }

    fun clearSelection() {
        selectedCategories.value = emptyList()
    }
}