package com.example.storyefun.data.models

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
    var volume: List<Volume> = emptyList(),
    var categoryIDs: List<String> = emptyList(), // for get list of category id from firebase
    var category: List<Category> = emptyList(), // for save catogory list as obj
    var comments: MutableList<Comment> = mutableListOf(), // Make sure this is mutable
) {
    fun isNovel(): Boolean {
        return category.any { it.name.equals("Novel", ignoreCase = true) }
    }

}