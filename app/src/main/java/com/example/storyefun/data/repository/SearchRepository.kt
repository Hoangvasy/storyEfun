package com.example.storyefun.data.repository

import com.example.storyefun.data.models.Book
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class SearchRepository(private val db: FirebaseFirestore) {

    suspend fun searchBooks(query: String): List<Book> {
        return try {
            val snapshot = db.collection("books")
                .whereGreaterThanOrEqualTo("name", query)
                .whereLessThan("name", query + '\uf8ff')
                .get()
                .await()

            snapshot.documents.map { doc ->
                Book(
                    id = doc.id,
                    name = doc.getString("name") ?: "Unknown",
                    author = doc.getString("author") ?: "Unknown",
                    description = doc.getString("description") ?: "No Description",
                    imageUrl = doc.getString("imageUrl") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
