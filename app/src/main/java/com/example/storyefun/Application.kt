package com.example.storyefun

import android.app.Application
import com.cloudinary.android.MediaManager
import com.google.firebase.FirebaseApp

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this) // Initialize Firebase
        // Initialize Cloudinary
        MediaManager.init(
            this,
            mapOf(
                "cloud_name" to "dytggtwgy",
                "api_key" to "516629548733734",
                "api_secret" to "V-0uQtA3HDbBWrjb_LUeKB2jzKo",
                "secure" to true // Force HTTPS URLs
            )
        )
    }
}