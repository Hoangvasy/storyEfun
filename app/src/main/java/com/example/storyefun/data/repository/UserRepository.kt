package com.example.storyefun.data.repository

import com.example.storyefun.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {
private val auth = FirebaseAuth.getInstance()
private val firestore = FirebaseFirestore.getInstance()

suspend fun getCurrentUser(): User? {
    val user = auth.currentUser ?: return null

    // Lấy thông tin cơ bản từ Auth
    val uid = user.uid
    val email = user.email ?: ""
    val username = user.displayName ?: ""

    // Lấy thêm coin từ Firestore
    val userDoc = firestore.collection("users").document(uid).get().await()

    val coin = userDoc.getLong("coin")?.toInt() ?: 0

    return User(
        uid = uid,
        email = email,
        name = username,
        coin = coin
    )
}


}