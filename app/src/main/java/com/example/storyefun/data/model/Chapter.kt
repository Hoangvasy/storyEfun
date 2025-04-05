package com.example.storyefun.data.model

sealed class Chapter {
    abstract val id: String
    abstract val name: String
}

data class NovelChapter(
    override val id: String,
    override val name: String,
    val content: List<ChapterContent> // List of text and optional images
) : Chapter()

data class MangaChapter(
    override val id: String,
    override val name: String,
    val pages: List<String> // List of image URLs
) : Chapter()


data class ChapterContent(
    val text: String? = null, // Text content (nullable)
    val imageUrl: String? = null // Image URL (nullable)
)