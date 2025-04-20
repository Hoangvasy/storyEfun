package com.example.storyefun.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel: ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _filteredBooks = MutableStateFlow<List<Book>>(emptyList())
    val filteredBooks: StateFlow<List<Book>> get() = _filteredBooks

    fun searchBooks(query: String) {
        viewModelScope.launch {
            if (query.isEmpty()) {
                fetchAllBooks()
            } else {
                firestore.collection("books")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + '\uf8ff') // Tìm kiếm theo tên
                    .get()
                    .addOnSuccessListener { result ->
                        val books = result.documents.mapNotNull { it.toObject(Book::class.java) }
                        _filteredBooks.value = books
                    }
                    .addOnFailureListener { exception ->
                        // Xử lý lỗi
                        _filteredBooks.value = emptyList()
                    }
            }
        }
    }

    private fun fetchAllBooks() {
        firestore.collection("books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.documents.mapNotNull { it.toObject(Book::class.java) }
                _filteredBooks.value = books
            }
            .addOnFailureListener { exception ->
                // Xử lý lỗi
                _filteredBooks.value = emptyList()
            }
    }
}
