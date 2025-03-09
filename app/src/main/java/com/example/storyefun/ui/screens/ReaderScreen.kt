package com.example.storyefun.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@Composable
fun ReaderScreen(navController: NavController) {
    var isUIVisible by remember { mutableStateOf(true) } // Controls visibility of header/footer
    val isManga = false
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { isUIVisible = !isUIVisible },
                indication = null, //  Remove default ripple effect
                interactionSource = remember { MutableInteractionSource() } // Prevents pressed state effect
            )
    ) {
        Column {
            CustomTopBar(isUIVisible, onBack = { /* Handle Back */ })
            Box(modifier = Modifier.weight(1f)) {
                if (isManga) {
                    MangaContent()
                } else {
                    NovelContent()
                }
            }
            CustomBottomBar(isUIVisible)
        }
    }
}


@Composable
fun NovelContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        items(100) { // Sample paragraphs
            Text(
                text = "Đây là truyện chữ là truyện chữ là truyện chữ...",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}

@Composable
fun MangaContent() {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        val images = listOf(
            "https://your-image-url.com/page1.jpg",
            "https://your-image-url.com/page2.jpg"
        )

        items(images) { imageUrl ->
            Image(
                painter = rememberImagePainter(imageUrl),
                contentDescription = "Manga Page",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CustomBottomBar(isVisible: Boolean) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { /* Previous Chapter */ }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous", tint = Color.Black)
                }
                Text("Chương trước", fontSize = 14.sp, color = Color.Black)
                Spacer(modifier = Modifier.weight(1f))
                Text("Chương sau", fontSize = 14.sp, color = Color.Black)
                IconButton(onClick = { /* Next Chapter */ }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next", tint = Color.Black)
                }
            }
        }
    }
}
@Composable
fun CustomTopBar(isVisible: Boolean, onBack: () -> Unit) {
    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Chương 1: Mở đầu", fontWeight = FontWeight.Bold)
            }
        }
    }
}


@Preview
@Composable
fun PreviewReaderScreen() {
    //ReaderScreen(isManga = false) // Toggle between true (Manga) and false (Novel)
}
