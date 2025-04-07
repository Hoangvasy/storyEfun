package com.example.storyefun.data

data class Chapter(
    val id: String = "",        // Default value
    val name: String = "",      // Default value
    val content: List<String> = emptyList()
)

