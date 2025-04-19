package com.example.storyefun.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.models.Book
import com.example.storyefun.data.repository.BookRepository
import com.example.storyefun.data.models.Chapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

            // Debugging log to check if result has comments
            if (result != null) {
                Log.d("info of loaded book comment : ", result.comments.toString())
            } else {
                Log.d("info", "No book found for ID: $bookId")
            }

            _book.value = result // Set the book after fetching comments
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

    // Helper function to get chapter content
    fun getChapterContent(volumeOrder: Int, chapterOrder: Int): Chapter? {
        val currentBook = _book.value ?: return null
        val volume = currentBook.volume.find { it.order == volumeOrder }
        return volume?.chapters?.find { it.order == chapterOrder }
    }

    // Helper function to get previous chapter
    fun getPreviousChapter(volumeOrder: Int, chapterOrder: Int): Triple<Boolean, Int?, Int?> {
        val currentBook = _book.value ?: return Triple(false, null, null)

        // Sort volumes by name to ensure consistent order
        val sortedVolumes = currentBook.volume.sortedBy { it.name }
        val volumeIndex = sortedVolumes.indexOfFirst { it.order == volumeOrder }
        if (volumeIndex == -1) return Triple(false, null, null)

        val volume = sortedVolumes[volumeIndex]
        // Sort chapters by order to ensure consistent order
        val sortedChapters = volume.chapters.sortedBy { it.order }
        val chapterIndex = sortedChapters.indexOfFirst { it.order == chapterOrder }
        if (chapterIndex == -1) return Triple(false, null, null)

        // If there's a previous chapter in the same volume
        if (chapterIndex > 0) {
            return Triple(true, volumeOrder, sortedChapters[chapterIndex - 1].order)
        }

        // If we're at the first chapter of the volume, go to the last chapter of the previous volume
        if (volumeIndex > 0) {
            val prevVolume = sortedVolumes[volumeIndex - 1]
            val sortedPrevChapters = prevVolume.chapters.sortedBy { it.order }
            val lastChapter = sortedPrevChapters.lastOrNull()
            return if (lastChapter != null) {
                Triple(true, prevVolume.order, lastChapter.order)
            } else {
                Triple(false, null, null)
            }
        }

        return Triple(false, null, null)
    }

    // Helper function to get next chapter
    fun getNextChapter(volumeOrder: Int, chapterOrder: Int): Triple<Boolean, Int?, Int?> {
        val currentBook = _book.value ?: return Triple(false, null, null)

        // Sort volumes by name to ensure consistent order
        val sortedVolumes = currentBook.volume.sortedBy { it.order }
        val volumeIndex = sortedVolumes.indexOfFirst { it.order == volumeOrder }
        if (volumeIndex == -1) return Triple(false, null, null)

        val volume = sortedVolumes[volumeIndex]
        // Sort chapters by order to ensure consistent order
        val sortedChapters = volume.chapters.sortedBy { it.order }
        val chapterIndex = sortedChapters.indexOfFirst { it.order == chapterOrder }
        if (chapterIndex == -1) return Triple(false, null, null)

        // If there's a next chapter in the same volume
        if (chapterIndex < sortedChapters.size - 1) {
            return Triple(true, volumeOrder, sortedChapters[chapterIndex + 1].order)
        }

        // If we're at the last chapter of the volume, go to the first chapter of the next volume
        if (volumeIndex < sortedVolumes.size - 1) {
            val nextVolume = sortedVolumes[volumeIndex + 1]
            val sortedNextChapters = nextVolume.chapters.sortedBy { it.order }
            val firstChapter = sortedNextChapters.firstOrNull()
            return if (firstChapter != null) {
                Triple(true, nextVolume.order, firstChapter.order)
            } else {
                Triple(false, null, null)
            }
        }

        return Triple(false, null, null)
    }



}