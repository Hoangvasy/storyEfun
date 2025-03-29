package com.example.storyefun.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.tasks.await

class BookRepository {
    private val db = Firebase.firestore

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
    fun addBook(book: Book)
    {

    }
    fun addVolume(volume: Volume, book: Book)
    {

    }
    fun addChapter(chapter: Chapter, volume: Volume, book: Book)
    {

    }
    fun deleteBook()
    {

    }



}