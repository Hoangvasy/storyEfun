package com.example.storyefun.data

import coil.annotation.ExperimentalCoilApi
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class CategoryRepository {
    val db = Firebase.firestore
    suspend fun addCategory(category: Category) {
        try {
            db.collection("category").document(category.id).set(category).await()
            println("Category added successfully")
        } catch (e: Exception) {
            println("Error when adding category: ${e.message}")
        }
    }

    suspend fun deleteCategory(category: Category) {
        try {
            db.collection("category").document(category.id).delete().await()
            println("Category deleted successfully")
        } catch (e: Exception) {
            println("Error when deleting category: ${e.message}")
        }
    }

    suspend fun editCategory(category: Category) {
        try {
            db.collection("category").document(category.id).set(category).await()
            println("Category updated successfully")
        } catch (e: Exception) {
            println("Error when editing category: ${e.message}")
        }
    }


}