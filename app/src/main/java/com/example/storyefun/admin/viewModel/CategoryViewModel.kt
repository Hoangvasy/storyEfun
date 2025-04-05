package com.example.storyefun.admin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.storyefun.data.model.Category
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CategoryViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchCategories()
    }

    fun fetchCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val snapshot = db.collection("category").get().await()
                _categories.value = snapshot.documents.mapNotNull { it.toObject(Category::class.java) }
            } catch (e: Exception) {
                println("Error fetching categories: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun addCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("category").document(category.id).set(category).await()
                fetchCategories() // Refresh the list
            } catch (e: Exception) {
                println("Error adding category: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("category").document(category.id).delete().await()
                fetchCategories()
            } catch (e: Exception) {
                println("Error deleting category: ${e.message}")
            }
            _isLoading.value = false
        }
    }

    fun editCategory(category: Category) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("category").document(category.id).set(category).await()
                fetchCategories()
            } catch (e: Exception) {
                println("Error editing category: ${e.message}")
            }
            _isLoading.value = false
        }
    }
}
