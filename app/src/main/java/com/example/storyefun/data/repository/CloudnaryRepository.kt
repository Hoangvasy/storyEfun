package com.example.storyefun.data.repository

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class CloudnaryRepository {



    suspend fun uploadImage(uri: Uri, folder: String): String {
        var resultUrl: String? = ""
        try {
            val result = MediaManager.get().upload(uri)
                .option("folder", folder)
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {
                        println("Upload started: $requestId")
                    }

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                        println("Upload progress: $bytes/$totalBytes")
                    }

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val imageUrl = (resultData["url"] as String)?.replace("http://", "https://")
                        resultUrl = imageUrl
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        println("Upload error: ${error.description}")
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {
                        println("Upload rescheduled: ${error.description}")
                    }
                })
                .dispatch()
        } catch (e: Exception) {
            print("Error when upload image ${uri} , ${e.message}")
        }
        return ""

    }
}