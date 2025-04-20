package com.example.storyefun.data.repository

import com.example.storyefun.data.models.Category
import com.google.firebase.firestore.FirebaseFirestore

//class CategoryRepository {
    fun CategoryFirebase (category: Category, callback: (Boolean) -> Unit) {
        val database = FirebaseFirestore.getInstance()
        val categoryRef = database.collection("categories")

        // tạo id tự động
        val categoryID = category.id.ifEmpty {
            categoryRef.document().id
        }
        val newCategory = category.copy(id = categoryID)

        // thêm category vào firebase
        categoryRef.document(categoryID).set(newCategory)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
//}