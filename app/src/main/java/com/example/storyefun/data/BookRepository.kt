package com.example.storyefun.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
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
            Log.e("error when getting books","Error when getting book:   ${e.message}")
            emptyList()
        }

    }
    suspend fun getBook(bookId : String) : Book? {
        try {
            val bookRef = db.collection("books").document(bookId)
            val book = bookRef.get().await()
            val result = book.toObject(Book::class.java)
            if (result != null)
            {
                val volumesSnapshot = bookRef.collection("volumes").get().await()
                val volumeList = mutableListOf<Volume>()

                for (volumeDoc in volumesSnapshot.documents) {
                    var volume = volumeDoc.toObject(Volume::class.java)
                    if (volume != null) {
                        // Fetch chapters for this volume
                        val chaptersSnapshot = volumeDoc.reference.collection("chapters").get().await()
                        val chapterList = chaptersSnapshot.mapNotNull { it.toObject(Chapter::class.java) }

                        volume.chapters = chapterList
                        volumeList.add(volume)
                    }
                }
                result.volume = volumeList

                // 2. Get full category objects from IDs
                val categoryIds = result.categoryIDs ?: emptyList()
                val categoryList = mutableListOf<Category>()
                for (categoryId in categoryIds) {
                    val catSnapshot = db.collection("category").document(categoryId).get().await()
                    catSnapshot.toObject(Category::class.java)?.let { categoryList.add(it) }
                }
                result.category = categoryList // <- Gắn vào một field mới




            }
            return result

        }
        catch (e : Exception)
        {
            Log.e("Error", e.message.toString())
            return null
        }
    }

    fun getBookByUser(): LiveData<List<Book>> {
        val liveData = MutableLiveData<List<Book>>()

        db.collection("books")
            .get()
            .addOnSuccessListener { result ->
                val books = result.documents.mapNotNull { document ->
                    val name = document.getString("name")
                    val posterUrl = document.getString("posterUrl") // hoặc "imageUrl"

                    if (name != null && posterUrl != null) {
                        Book(name, posterUrl)
                    } else null // bỏ qua nếu thiếu dữ liệu
                }
                liveData.postValue(books)
            }
            .addOnFailureListener { exception ->
                Log.e("BookRepository", "Error fetching books", exception)
                liveData.postValue(emptyList())
            }

        return liveData
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



}