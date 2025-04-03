package com.example.storyefun.data

import android.util.Log
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

    suspend fun getBooks() : List<Book>{
        return try {
            val snapshot = db.collection("books").get().await()
            snapshot.documents.mapNotNull { it.toObject(Book::class.java) }

        } catch (e : Exception)
        {
            println("Error when getting book:   ${e.message}")
            emptyList()
        }

    }

    fun getBookByUser()
    {

    }
    suspend fun addBook(book: Book)
    {

            try {
                // Add book to Firestore
                db.collection("books").document(book.id).set(book).await()
                Log.d("Success","Book added successfully!")
            } catch (e: Exception) {
                Log.d("Error","Error adding book: ${e.message}")
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