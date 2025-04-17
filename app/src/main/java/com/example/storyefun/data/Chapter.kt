package com.example.storyefun.data

data class Chapter(
    val id: String = "",        // Default value
    val title: String = "",      // Default value
    val order: Int = 0,
    val content: List<String> = emptyList()
)

