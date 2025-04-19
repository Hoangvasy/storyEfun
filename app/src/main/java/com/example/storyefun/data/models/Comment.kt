package com.example.storyefun.data.models

import com.google.firebase.auth.FirebaseUser

data class Comment(
    val userId: String = "",
    val userImageUrl: String = "",
    val userName: String = "",
    val date: String = "",
    val content: String = "",
    val type: String = "text",
)

