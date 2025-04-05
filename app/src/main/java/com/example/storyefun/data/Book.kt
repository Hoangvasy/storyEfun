package com.example.storyefun.data

data class Book(
    val id: String = "",
    val name: String = "",
    val author: String = "",
    val description: String = "",
    val imageUrl: String? = null, // Book cover image URL
    val posterUrl: String? = null, // Book poster image URL
    val type: String = "",
    val follows: Int = 0,
    val likes: Int = 0,
    val views: Int = 0,
    val volume: List<Volume> = emptyList()
)