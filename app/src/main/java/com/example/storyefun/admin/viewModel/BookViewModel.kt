package com.example.storyefun.admin.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.Book
import com.example.storyefun.data.BookRepository
import com.example.storyefun.data.CloudnaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class BookViewModel() : ViewModel()
{
    private val cloudnaryRepository: CloudnaryRepository = CloudnaryRepository()
    private val bookRepository: BookRepository = BookRepository()
    private val _books = MutableLiveData<List<Book>>()
    val books : LiveData<List<Book>> get() = _books
    val _isLoading : MutableLiveData<Boolean> = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading


    init {
        viewModelScope.launch {
            setState(true)
            _books.value = bookRepository.getBooks()
            setState(false)
        }

    }
    fun addBook(bookId: String) {

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
    fun addBook (book: Book, coverUri : Uri, posterUri: Uri)
    {
        viewModelScope.launch {
            _isLoading.value = true
            val coverUrl =  cloudnaryRepository.uploadImage(coverUri, "Books")
            val posterUrl = cloudnaryRepository.uploadImage(coverUri, "Books")
            val newBook = book.copy(
                imageUrl = coverUrl,
                posterUrl = posterUrl
            )
            Log.d("Success","Book adding move to view model!")

            bookRepository.addBook(newBook)
            delay(2000)
            _isLoading.value = false
        }
    }

    fun setState(state : Boolean)
    {
        _isLoading.value = state
    }



}