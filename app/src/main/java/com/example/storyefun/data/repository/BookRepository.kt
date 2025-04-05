package com.example.storyefun.data.repository

import androidx.lifecycle.MutableLiveData
import com.example.storyefun.data.model.Book
import com.example.storyefun.data.model.Chapter
import com.example.storyefun.data.model.Volume
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class BookRepository {
    private val db = Firebase.firestore
    val isLoading = MutableLiveData(false)

    suspend fun getBooks() : List<Book>{
        return try {
            val snapshot = db.collection("books").get().await()
            snapshot.documents.mapNotNull { it.toObject(Book::class.java) }
            snapshot.documents.mapNotNull { doc ->
                val book = doc.toObject(Book::class.java)
                book?.copy(id = doc.id) // inject the Firestore document ID into the Book object
            }

        } catch (e : Exception)
        {
            println("Error when getting book:   ${e.message}")
            emptyList()
        }

    }

    fun getBookByUser()
    {

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
    fun addVolume(volume: Volume, book: Book)
    {

    }
    fun addChapter(chapter: Chapter, volume: Volume, book: Book)
    {

    }
    suspend fun deleteBook (bookId : String) : Boolean
    {
        try {
            db.collection("books").document(bookId).delete().await()
            return true
        }
        catch (e : Exception)
        {
            println("Error when deleting book ${e.message}")

        }
        return false

    }



}