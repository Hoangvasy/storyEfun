package com.example.storyefun.utils

import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URLConnection
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.File
import java.util.zip.ZipInputStream

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
suspend fun downloadAndExtractDocx(url: String): String {
    return try {
        // Make sure the network operation happens on a background thread
        withContext(Dispatchers.IO) {
            val stream = BufferedInputStream(URL(url).openStream())  // Wrap in BufferedInputStream for better performance
            extractTextFromDocx(stream)
        }
    } catch (e: Exception) {
        // Log full exception stack trace for debugging purposes
        e.printStackTrace()

        // Return the error message if any exception occurs during download or extraction
        "Error downloading or extracting DOCX: ${e.localizedMessage ?: "Unknown error"}"
    }
}

fun extractTextFromDocx(inputStream: InputStream): String {
    val zipInputStream = ZipInputStream(inputStream)
    var entry = zipInputStream.nextEntry
    val text = StringBuilder()

    try {
        // Loop through entries in the DOCX zip file
        while (entry != null) {
            if (entry.name == "word/document.xml") {
                // Initialize XML parser
                val factory = XmlPullParserFactory.newInstance()
                val parser = factory.newPullParser()
                parser.setInput(zipInputStream, "UTF-8")

                var eventType = parser.eventType
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG && parser.name == "w:t") {
                        parser.next()
                        if (parser.eventType == XmlPullParser.TEXT) {
                            text.append(parser.text).append(" ")
                        }
                    }
                    eventType = parser.next()
                }
                break
            }
            entry = zipInputStream.nextEntry
        }
    } catch (e: Exception) {
        // Log full exception stack trace for debugging purposes
        e.printStackTrace()

        // Catch XML parsing errors and provide more context
        return "Error parsing DOCX content: ${e.localizedMessage ?: "Unknown XML parsing error"}"
    } finally {
        zipInputStream.close()  // Ensure the stream is closed even if an error occurs
    }

    return text.toString().trim()  // Remove any trailing whitespace
}
