package com.example.storyefun.utils

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

// Hàm để tải nội dung file .txt từ URL (có thể dùng lại)
suspend fun downloadTextFile(url: String): String {
    return withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.connect()
        connection.inputStream.bufferedReader().use { it.readText() }
    }
}

// Composable để hiển thị PDF từ URL trong WebView
@Composable
fun PdfViewer(url: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                settings.javaScriptEnabled = true
                loadUrl("https://docs.google.com/viewer?url=$url")
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
