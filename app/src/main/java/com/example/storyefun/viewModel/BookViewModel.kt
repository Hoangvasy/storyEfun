package com.example.storyefun.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.Book
import com.example.storyefun.data.BookRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookViewModel() : ViewModel()
{
    private val bookRepository: BookRepository = BookRepository()
    private val _books = MutableLiveData<List<Book>>()
    val books : LiveData<List<Book>> get() = _books
    private val _book = MutableLiveData<Book?>()
    val book: LiveData<Book?> get() = _book


    val _isLoading : MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        viewModelScope.launch {
            setState(true)
            _books.value = bookRepository.getBooks()
            setState(false)
        }

    }
    fun fetchBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = bookRepository.getBook(bookId)
            _book.value = result
            Log.e("info of loaded book: ", result.toString())
            _isLoading.value = false
        }
    }

    fun addBook(bookId: String) {

    }
    fun updateBook(book: Book) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000)
            bookRepository.updateBook(book)
            _books.value = bookRepository.getBooks()
            _isLoading.value = false
        }
    }
    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            delay(2000) // Simulate API call
            bookRepository.deleteBook(bookId)
            _books.value = bookRepository.getBooks() // Reload books after deletion
            _isLoading.value = false
        }
    }


    fun setState(state : Boolean)
    {
        _isLoading.value = state
    }



}