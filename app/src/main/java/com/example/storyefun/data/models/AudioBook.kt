package com.example.storyefun.data.models

import com.google.gson.annotations.SerializedName

//data class AudioBook(
//    @SerializedName("id")  val id: Int,
//    @SerializedName("name") val name: String,
//    @SerializedName("author") val author: String,
//    @SerializedName("image") val image: String,
//    @SerializedName("audio") val audio: String,
//    @SerializedName("created_at") val created_at: String,
//    @SerializedName("updated_at") val updated_at: String
//)

data class AudioBook(

    val name: String,
    val author: String,
    val image: String,
    val audio: String,
    val id: Int
//    val created_at: String,
//    val updated_at: String
)


