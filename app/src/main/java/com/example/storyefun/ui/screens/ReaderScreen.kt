package com.example.storyefun.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.BookViewModel

@Composable
fun ReaderScreen(
    navController: NavController,
    bookId: String,
    volumeOrder: Int,
    chapterOrder: Int,
    viewModel: BookViewModel = viewModel()
) {
    var isUIVisible by remember { mutableStateOf(true) }
    val book by viewModel.book.observeAsState()

    var currentVolumeOrder by remember { mutableStateOf(volumeOrder) }
    var currentChapterOrder by remember { mutableStateOf(chapterOrder) }

    LaunchedEffect(bookId) {
        viewModel.fetchBook(bookId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = { isUIVisible = !isUIVisible },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Column {
            val chapter = viewModel.getChapterContent(currentVolumeOrder, currentChapterOrder)
            CustomTopBar(
                isVisible = isUIVisible,
                chapterName = chapter?.title ?: "Loading...",
                onBack = { navController.popBackStack() }
            )

            Box(modifier = Modifier.weight(1f)) {
                if (book != null) {
                    val isManga = !book!!.isNovel()
                    val chapterContent = viewModel.getChapterContent(currentVolumeOrder, currentChapterOrder)

                    if (chapterContent != null) {
                        if (isManga) {
                            MangaContent(chapterContent.content)
                        } else {
                            NovelContent(chapterContent.content)
                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chapter not found" + chapterOrder + volumeOrder)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (book != null) {
                CustomBottomBar(
                    isVisible = isUIVisible,
                    viewModel = viewModel,
                    volumeOrder = currentVolumeOrder,  // Pass currentVolumeOrder
                    chapterOrder = currentChapterOrder,  // Pass currentChapterOrder
                    onPreviousChapter = {
                        val (hasPrevious, prevVolumeOrder, prevChapterOrder) = viewModel.getPreviousChapter(currentVolumeOrder, currentChapterOrder)
                        if (hasPrevious && prevVolumeOrder != null && prevChapterOrder != null) {
                            currentVolumeOrder = prevVolumeOrder
                            currentChapterOrder = prevChapterOrder
                        }
                    },
                    onNextChapter = {
                        val (hasNext, nextVolumeOrder, nextChapterOrder) = viewModel.getNextChapter(currentVolumeOrder, currentChapterOrder)
                        if (hasNext && nextVolumeOrder != null && nextChapterOrder != null) {
                            currentVolumeOrder = nextVolumeOrder
                            currentChapterOrder = nextChapterOrder
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CustomTopBar(
    isVisible: Boolean,
    chapterName: String,
    onBack: () -> Unit
) {
    val theme = LocalAppColors.current
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(theme.background)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }
                Text(
                    text = chapterName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun CustomBottomBar(
    isVisible: Boolean,
    viewModel: BookViewModel,
    volumeOrder: Int,
    chapterOrder: Int,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit
) {
    val theme = LocalAppColors.current

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(theme.background)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (hasPrevious, _, _) = viewModel.getPreviousChapter(volumeOrder, chapterOrder)
                IconButton(
                    onClick = { if (hasPrevious) onPreviousChapter() },
                    enabled = hasPrevious
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = if (hasPrevious) Color.Black else Color.Gray
                    )
                }
                Text(
                    text = "Chương trước",
                    fontSize = 14.sp,
                    color = if (hasPrevious) Color.Black else Color.Gray
                )

                Spacer(modifier = Modifier.weight(1f))

                val (hasNext, _, _) = viewModel.getNextChapter(volumeOrder, chapterOrder)
                Text(
                    text = "Chương sau",
                    fontSize = 14.sp,
                    color = if (hasNext) Color.Black else Color.Gray
                )
                IconButton(
                    onClick = { if (hasNext) onNextChapter() },
                    enabled = hasNext
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = if (hasNext) Color.Black else Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NovelContent(fileUrls: List<String>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(fileUrls) { url ->
            Text(
                text = "Content from $url",
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
fun MangaContent(imageUrls: List<String>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(imageUrls) { imageUrl ->
            AsyncImage(
                model = imageUrl,
                contentDescription = "Manga Page",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}