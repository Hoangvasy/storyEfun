package com.example.storyefun.data

import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class BookRepository {
    private val db = Firebase.firestore
    val isLoading = MutableLiveData(false)

    suspend fun getBooks(): List<Book> {
        return try {
            val snapshot = db.collection("books").get().await()
            snapshot.documents.mapNotNull { it.toObject(Book::class.java) }

        } catch (e: Exception) {
            println("Error when getting book:   ${e.message}")
            emptyList()
        }

    }

    fun getBookByUser() {

    }

    suspend fun addBook(book: Book): Boolean {
        return try {
            db.collection("books").add(book).await()  // Thêm sách vào Firestore
            true
        } catch (e: Exception) {
            println("Error when adding book: ${e.message}")
            false
        }
    }

    fun addVolume(volume: Volume, book: Book) {

    }

    suspend fun updateBook(book: Book): Boolean {
        return try {
            db.collection("books")
                .document(book.id)  // Use book's ID to identify the document
                .set(book)  // Replace the entire document with the new data
                .await()
            true
        } catch (e: Exception) {
            println("Error when updating book: ${e.message}")
            false
        }
    }

    fun addChapter(chapter: Chapter, volume: Volume, book: Book) {

    }

    suspend fun deleteBook(bookId: String): Boolean {
        try {
            db.collection("books").document(bookId).delete().await()
            return true
        } catch (e: Exception) {
            println("Error when deleting book ${e.message}")

        }
        return false

    }
}



