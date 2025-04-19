package com.example.storyefun.data.models

data class Chapter(
    val id: String = "",        // Default value
    val title: String = "",      // Default value
    val order: Long = 0,
    val content: List<String> = emptyList()
)
