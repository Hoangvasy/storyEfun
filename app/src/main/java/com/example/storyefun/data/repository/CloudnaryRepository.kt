package com.example.storyefun.data.repository

import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback

class CloudnaryRepository {
    suspend fun getImage(path: String) {
        // Placeholder for future implementation
    }

    // Upload image to Cloudinary
    suspend fun uploadToCloudinary(
        uri: Uri,
        folder: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        MediaManager.get().upload(uri)
            .option("folder", folder)
            .callback(object : UploadCallback {
                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val secureUrl = resultData["secure_url"] as String
                    onSuccess(secureUrl)
                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    Log.e("Cloudinary", "Upload error: ${error.description}")
                    onFailure(Exception(error.description))
                }

                override fun onStart(requestId: String) {
                    Log.d("Cloudinary", "Upload started: $requestId")
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    Log.d("Cloudinary", "Upload progress: $bytes/$totalBytes")
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    Log.w("Cloudinary", "Upload rescheduled: ${error.description}")
                }
            })
            .dispatch()
    }
}