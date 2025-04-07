package com.example.storyefun.data

data class Volume(
    var id: String = "",
    var name: String = "",
    var title: String = "",
    var chapters: List<Chapter> = emptyList()
)
