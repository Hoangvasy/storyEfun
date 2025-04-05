package com.example.storyefun.data.model

data class Volume (
    val id: String,
    val name: String,
    val chapters: List<Chapter> = emptyList()// List of chapters
)