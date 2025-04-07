package com.example.storyefun.data


data class Category (
    val id: String,
    val name: String,
    val description: String ,
    val imageUrl: String? = null, // Book cover image URL

)