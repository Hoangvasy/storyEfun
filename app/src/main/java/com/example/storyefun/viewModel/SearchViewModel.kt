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
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel {
    private val _books = MutableStateFlow<List<Book>>(emptyList()) // Dữ liệu gốc
    val books = _books.asStateFlow()

    private val _filteredBooks = MutableStateFlow<List<Book>>(emptyList()) // Dữ liệu lọc
    val filteredBooks = _filteredBooks.asStateFlow()

    fun searchBooks(query: String) {
        _filteredBooks.value = if (query.isBlank()) {
            _books.value
        } else {
            _books.value.filter { book ->
                book.name.contains(query, ignoreCase = true) ||
                        book.description.contains(query, ignoreCase = true)
            }
        }
    }

    // Hàm tải dữ liệu
    fun loadBooks() {
        // Giả lập dữ liệu
        _books.value = listOf(
            Book("1", "Book A", "Description A"),
            Book("2", "Book B", "Description B"),
            Book("3", "Book C", "Description C")
        )
        _filteredBooks.value = _books.value
    }
}
