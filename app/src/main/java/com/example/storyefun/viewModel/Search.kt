package com.example.storyefun.viewModel

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

suspend fun searchBooks(nameQuery: String): List<Book> {
    val firestore = FirebaseFirestore.getInstance()
    val result = mutableListOf<Book>()

    try {
        val snapshot = firestore.collection("books")
            .whereGreaterThanOrEqualTo("name", nameQuery)
            .whereLessThanOrEqualTo("name", nameQuery + "\uf8ff")
            .get()
            .await()

        for (document in snapshot.documents) {
            document.toObject(Book::class.java)?.let { result.add(it) }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return result
}

suspend fun searchCategory(categoryName: String): List<Book> {
    val firestore = FirebaseFirestore.getInstance()
    val result = mutableListOf<Book>()

    try {
        val categorySnapshot = firestore.collection("categories")
            .whereEqualTo("name", categoryName)
            .get()
            .await()

        val categoryId = categorySnapshot.documents.firstOrNull()?.id

        if (categoryId != null) {
            val bookSnapshot = firestore.collection("books")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .await()

            for (document in bookSnapshot.documents) {
                document.toObject(Book::class.java)?.let { result.add(it) }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return result
}