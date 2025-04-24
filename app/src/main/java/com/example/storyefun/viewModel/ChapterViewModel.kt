package com.example.storyefun.viewModel

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.models.Chapter
import com.example.storyefun.data.repository.ChapterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChapterViewModel(
    private val repository: ChapterRepository,
    private val bookId: String,
    private val volumeId: String
) : ViewModel() {

    private val _chapters = MutableStateFlow<List<Chapter>>(emptyList())
    val chapters: StateFlow<List<Chapter>> = _chapters

    private val _title = mutableStateOf("")
    val title: String get() = _title.value

    private val _chapterNumber = mutableStateOf(1)
    val chapterNumber: Int get() = _chapterNumber.value

    private val _imageUris = mutableStateOf<List<Uri>>(emptyList())
    val imageUris: List<Uri> get() = _imageUris.value

    private val _isUploading = mutableStateOf(false)
    val isUploading: Boolean get() = _isUploading.value

    init {
        // Lấy danh sách chương khi ViewModel khởi tạo
        viewModelScope.launch {
            repository.getChapters(bookId, volumeId).collect { chapters ->
                _chapters.value = chapters
                _chapterNumber.value = chapters.size + 1
                _title.value = "Chapter ${_chapterNumber.value}"
            }
        }
    }

    fun updateTitle(newTitle: String) {
        _title.value = newTitle
    }

    fun updateImageUris(uris: List<Uri>) {
        _imageUris.value = uris
    }

    fun uploadChapter(onComplete: () -> Unit) {
        viewModelScope.launch {
            _isUploading.value = true
            val imageUrls = mutableListOf<String>()
            _imageUris.value.forEach { uri ->
                repository.uploadImageToCloudinary(uri, "chapters").collect { url ->
                    imageUrls.add(url)
                    if (imageUrls.size == _imageUris.value.size) {
                        repository.uploadChapter(bookId, volumeId, _title.value, imageUrls) {
                            _title.value = "Chapter ${_chapterNumber.value + 1}"
                            _imageUris.value = emptyList()
                            onComplete()
                        }
                    }
                }
            }
            _isUploading.value = false
        }
    }
}