package com.example.storyefun.data.models

import com.google.firebase.auth.FirebaseUser

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val coin: Int = 0,
    val likedBooks: List<String> = emptyList(),
    val followedBooks: List<String> = emptyList()
)
fun mapFirebaseUserToAppUser(firebaseUser: FirebaseUser): User {
    return User(
        uid = firebaseUser.uid,
        name = firebaseUser.displayName ?: "",
        email = firebaseUser.email ?: "",
        photoUrl = firebaseUser.photoUrl?.toString() ?: "",
        coin = 0, // mặc định 0, sẽ cập nhật từ Firestore nếu có
        likedBooks = emptyList(),
        followedBooks = emptyList()
    )
}
