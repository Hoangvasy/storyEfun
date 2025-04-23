package com.example.storyefun.viewModel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.storyefun.data.models.Book
import com.example.storyefun.data.repository.BookRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class UploadViewModel(
    private val repository: BookRepository = BookRepository()
) : ViewModel() {
    var bookName by mutableStateOf("")
    var authorName by mutableStateOf("")
    var description by mutableStateOf("")
    var selectedCategory by mutableStateOf<List<String>>(emptyList())
    var imageUri by mutableStateOf<Uri?>(null)
    var posterUri by mutableStateOf<Uri?>(null)

    var isUploading by mutableStateOf(false)
    var uploadSuccess by mutableStateOf<Boolean?>(null)

    fun uploadBook(context: Context, navController: NavController) {
        if (imageUri == null || posterUri == null) {
            Toast.makeText(context, "Please select both images", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedCategory.isEmpty()) {
            Toast.makeText(context, "Please select at least one category", Toast.LENGTH_SHORT).show()
            return
        }

        isUploading = true
        val bookId = UUID.randomUUID().toString()

        repository.uploadImageToCloudinary(imageUri!!, "book_covers", { imgUrl ->
            repository.uploadImageToCloudinary(posterUri!!, "book_posters", { posterUrl ->
                val book = Book(
                    id = bookId,
                    name = bookName,
                    author = authorName,
                    description = description,
                    imageUrl = imgUrl,
                    posterUrl = posterUrl,
                    type = "novel",
                    categoryIDs = selectedCategory
                )

                repository.uploadBook(book, {
                    isUploading = false
                    uploadSuccess = true
                    Toast.makeText(context, "Book uploaded successfully!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }, { err ->
                    isUploading = false
                    uploadSuccess = false
                    Toast.makeText(context, "Upload failed: $err", Toast.LENGTH_SHORT).show()
                })
            }, { err ->
                isUploading = false
                Toast.makeText(context, "Poster upload failed: $err", Toast.LENGTH_SHORT).show()
            })
        }, { err ->
            isUploading = false
            Toast.makeText(context, "Image upload failed: $err", Toast.LENGTH_SHORT).show()
        })
    }
}