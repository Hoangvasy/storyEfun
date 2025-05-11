package com.example.storyefun.data.rest

import com.example.storyefun.data.models.AudioBook
import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Response

interface ApiService {
    @GET("audiobooks/audiobook")
//    fun getAudioBooks(): Call<List<AudioBook>>
    suspend fun fetchData(): List<AudioBook>
}


