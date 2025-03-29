package com.example.storyefun.admin.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.Book
import com.example.storyefun.data.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val bookRepository = BookRepository()
    private val _books = MutableLiveData<List<Book>>()
    val books : LiveData<List<Book>> get() = _books
    init {
        viewModelScope.launch {
            _books.value = bookRepository.getBooks()
        }

    }
}