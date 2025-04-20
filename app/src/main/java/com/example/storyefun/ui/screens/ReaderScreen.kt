package com.example.storyefun.ui.screens

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.storyefun.R
import com.example.storyefun.data.models.Book
import com.example.storyefun.ui.theme.AppColors
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.utils.downloadAndExtractDocx
import com.example.storyefun.utils.downloadTextFile
import com.example.storyefun.viewModel.BookViewModel
import com.example.storyefun.viewModel.ThemeViewModel
import kotlinx.coroutines.launch



@Composable
fun ReaderScreen(
    navController: NavController,
    bookId: String,
    volumeOrder: Long,
    chapterOrder: Long,
    themeViewModel: ThemeViewModel,

    viewModel: BookViewModel = viewModel(),
) {
    var isUIVisible by remember { mutableStateOf(true) }
    val book by viewModel.book.observeAsState()
    val theme = LocalAppColors.current

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

            Box(modifier = Modifier.weight(1f)
                .background(theme.backgroundColor)

            ) {
                if (book != null) {
                    val isManga = !book!!.isNovel()
                    val chapterContent = viewModel.getChapterContent(currentVolumeOrder, currentChapterOrder)

                    if (chapterContent != null) {
                        if (isManga) {
                            MangaContent(chapterContent.content)
                        } else {
                            Log.d("novel check", "this book is novel")
                            NovelContent(chapterContent.content, viewModel.fontSize.value, viewModel.lineSpacing.value)                        }
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Chapter not found", color = theme.textPrimary)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = theme.textPrimary)
                    }
                }
            }

            if (book != null) {
                CustomBottomBar(
                    isVisible = isUIVisible,
                    viewModel = viewModel,
                    volumeOrder = currentVolumeOrder,
                    chapterOrder = currentChapterOrder,
                    book = book,
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
                    },
                    themeViewModel = themeViewModel
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
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val baseHeight = 56.dp
    val adjustedHeight = baseHeight / fontScale

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(adjustedHeight)
                .background(theme.background)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = theme.textPrimary
                    )
                }
                Text(
                    text = chapterName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = theme.textPrimary
                )
            }
        }
    }
}
@Composable
fun CustomBottomBar(
    isVisible: Boolean,
    viewModel: BookViewModel,
    volumeOrder: Long,
    chapterOrder: Long,
    book: Book?,
    onPreviousChapter: () -> Unit,
    onNextChapter: () -> Unit,
    themeViewModel: ThemeViewModel,
) {
    val theme = LocalAppColors.current
    val configuration = LocalConfiguration.current
    val fontScale = configuration.fontScale
    val baseHeight = 64.dp
    val adjustedHeight = baseHeight / fontScale
    val fontSize by viewModel.fontSize
    val lineSpacing by viewModel.lineSpacing // Observe lineSpacing from ViewModel

    var darkMode by remember { mutableStateOf(false) }
    var fontSelectorExpanded by remember { mutableStateOf(false) }
    var lineSpacingSelectorExpanded by remember { mutableStateOf(false) }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(theme.background)
                .shadow(4.dp, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(adjustedHeight)
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Previous Chapter
                ChapterNavigationButton(
                    enabled = viewModel.getPreviousChapter(volumeOrder, chapterOrder).first,
                    icon = Icons.Default.ArrowBack,
                    text = "",
                    theme = theme,
                    onClick = onPreviousChapter
                )

                // Dark Mode Toggle
                IconButton(
                    onClick = {
                        darkMode = !darkMode
                        themeViewModel.toggleTheme()
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            theme.backgroundColor.copy(alpha = 0.1f),
                            CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = if (darkMode) R.drawable.dark else R.drawable.light),
                        contentDescription = "Toggle Dark Mode",
                        tint = theme.textPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Font Size Selector (for novels)
                if (book != null) {
                    if (book.isNovel()) {
                        FontSizeSelector(
                            fontSize = fontSize,
                            expanded = fontSelectorExpanded,
                            theme = theme,
                            onExpandedChange = { fontSelectorExpanded = it },
                            onFontSizeSelected = { size ->
                                viewModel.setFontSize(size) // Update fontSize in ViewModel
                                fontSelectorExpanded = false
                            }
                        )
                        LineSpacingSelector(
                            lineSpacing = lineSpacing,
                            expanded = lineSpacingSelectorExpanded,
                            theme = theme,
                            onExpandedChange = { lineSpacingSelectorExpanded = it },
                            onLineSpacingSelected = { spacing ->
                                viewModel.setLineSpacing(spacing)
                                lineSpacingSelectorExpanded = false
                            }
                        )
                    }
                }

                // Next Chapter
                ChapterNavigationButton(
                    enabled = viewModel.getNextChapter(volumeOrder, chapterOrder).first,
                    icon = Icons.AutoMirrored.Filled.ArrowForward,
                    text = "",
                    theme = theme,
                    onClick = onNextChapter

                )
            }
        }
    }
}

@Composable
private fun LineSpacingSelector(
    lineSpacing: Float,
    expanded: Boolean,
    theme: AppColors,
    onExpandedChange: (Boolean) -> Unit,
    onLineSpacingSelected: (Float) -> Unit,
) {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(theme.background.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "↕",
                fontSize = 14.sp,
                color = theme.textSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${String.format("%.1f", lineSpacing)}x",
                fontSize = 14.sp,
                color = theme.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .background(theme.background)
                .border(1.dp, theme.textSecondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            listOf(1.0f, 1.5f, 2.0f, 2.5f).forEach { spacing ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "${String.format("%.1f", spacing)}x",
                            fontSize = 14.sp,
                            color = theme.textPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    },
                    onClick = { onLineSpacingSelected(spacing) }
                )
            }
        }
    }
}
@Composable
private fun ChapterNavigationButton(
    enabled: Boolean,
    icon: ImageVector,
    text: String,
    theme: AppColors,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable(enabled = enabled) { onClick() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = if (enabled) theme.textPrimary else theme.textSecondary.copy(alpha = 0.5f),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = if (enabled) theme.textPrimary else theme.textSecondary.copy(alpha = 0.5f),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun FontSizeSelector(
    fontSize: Float,
    expanded: Boolean,
    theme: AppColors,
    onExpandedChange: (Boolean) -> Unit,
    onFontSizeSelected: (Float) -> Unit
) {
    Box {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(theme.background.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .clickable { onExpandedChange(!expanded) }
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Aa",
                fontSize = 14.sp,
                color = theme.textSecondary,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${fontSize.toInt()}sp",
                fontSize = 14.sp,
                color = theme.textPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier
                .background(theme.background)
                .border(1.dp, theme.textSecondary.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
        ) {
            listOf(12, 14, 16, 18, 20, 24, 26, 28, 30, 32, 34, 36, 38, 40).forEach { size ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "$size sp",
                            fontSize = 14.sp,
                            color = theme.textPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    },
                    onClick = { onFontSizeSelected(size.toFloat()) }
                )
            }
        }
    }
}
@Composable
fun NovelContent(
    fileUrls: List<String>,
    fontSize: Float, // Add fontSize parameter,
    lineSpacing: Float
) {
    val theme = LocalAppColors.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(fileUrls) { url ->
            var content by remember { mutableStateOf("Đang tải...") }

            LaunchedEffect(url) {
                if (url.endsWith(".txt")) {
                    content = try {
                        downloadTextFile(url)
                    } catch (e: Exception) {
                        "Lỗi tải file: ${e.message}"
                    }
                }
                else if (url.endsWith((".docx")))
                {
                    content = try {
                        downloadAndExtractDocx(url)
                    } catch (e: Exception) {
                        "Lỗi tải file: ${e.message}"
                    }
                }

                else {
                content = "Chưa hỗ trợ định dạng này: $url"
            }
            }

            Text(
                text = content,
                fontSize = fontSize.sp, // Use the passed fontSize
                color = theme.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp),
                lineHeight = (fontSize * lineSpacing).sp, // Apply line spacing
            )
        }
    }
}
/*
@Composable
fun NovelContent(fileUrls: List<String>) {

    val theme = LocalAppColors.current
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {
        items(fileUrls) { url ->
            var content by remember { mutableStateOf("Đang tải...") }

            LaunchedEffect(url) {
                if (url.endsWith(".txt")) {
                    content = try {
                        downloadTextFile(url)
                    } catch (e: Exception) {
                        "Lỗi tải file: ${e.message}"
                    }
                } else {
                    content = "Chưa hỗ trợ định dạng này: $url"
                }
            }

            Text(
                text = content,
                fontSize = 16.sp,
                color = theme.textPrimary,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
    }
}*/

@Composable
fun MangaContent(imageUrls: List<String>) {
    val scale = remember { Animatable(1f) }
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val screenWidthPx = with(LocalDensity.current) { screenWidthDp.toPx() }

    val isZoomed = scale.value > 1f
    val verticalScrollState = rememberScrollState()

    // Tổng chiều cao ảnh (dp)
    var totalContentHeightDp by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current // lấy trước


    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    coroutineScope.launch {
                        // Tính scale
                        val newScale = (scale.value * zoom).coerceIn(1f, 5f)
                        scale.snapTo(newScale)

                        if (newScale > 1f) {
                            // Tính giới hạn offset
                            val contentWidth = screenWidthPx * newScale
                            val contentHeight = with(density) { totalContentHeightDp.toPx() } * newScale

                            val maxOffsetX = ((contentWidth - screenWidthPx) / 2f).coerceAtLeast(0f)
                            val maxOffsetY = ((contentHeight - screenWidthPx * 2f) / 2f).coerceAtLeast(0f) // cho margin lớn hơn 1 tí

                            val newOffsetX = (offsetX.value + pan.x).coerceIn(-maxOffsetX, maxOffsetX)
                            val newOffsetY = (offsetY.value + pan.y).coerceIn(-maxOffsetY, maxOffsetY)

                            offsetX.snapTo(newOffsetX)
                            offsetY.snapTo(newOffsetY)
                        } else {
                            scale.snapTo(1f)
                            offsetX.snapTo(0f)
                            offsetY.snapTo(0f)
                        }
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        coroutineScope.launch {
                            scale.animateTo(1f)
                            offsetX.animateTo(0f)
                            offsetY.animateTo(0f)
                        }
                    }
                )
            }
            .clipToBounds()
    ) {
        // Scroll dọc khi chưa zoom
        val scrollModifier = if (!isZoomed) {
            Modifier.verticalScroll(verticalScrollState)
        } else {
            Modifier
        }

        var accumulatedHeight = 0.dp

        Column(
            modifier = scrollModifier
                .graphicsLayer {
                    scaleX = scale.value
                    scaleY = scale.value
                    translationX = offsetX.value
                    translationY = offsetY.value
                    transformOrigin = TransformOrigin(0.5f, 0f)
                }
        ) {
            imageUrls.forEach { imageUrl ->
                var imageHeight by remember { mutableStateOf(500.dp) }

                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Manga Page",
                    contentScale = ContentScale.FillWidth,
                    onSuccess = { state ->
                        val width = state.painter.intrinsicSize.width
                        val height = state.painter.intrinsicSize.height
                        if (width > 0) {
                            val ratio = height / width
                            imageHeight = screenWidthDp * ratio
                            // Cộng dồn chiều cao
                            accumulatedHeight += imageHeight + 8.dp
                            totalContentHeightDp = accumulatedHeight
                        }
                    },
                    modifier = Modifier
                        .width(screenWidthDp)
                        .height(imageHeight)
                        .padding(bottom = 8.dp)
                )
            }
        }
    }
}
