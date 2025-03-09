@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.storyefun.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.storyefun.R
import com.example.storyefun.ui.components.BottomBar
import com.example.storyefun.ui.components.Header
import com.example.storyefun.ui.theme.AppTheme
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.ui.theme.ThemeViewModel

@ExperimentalMaterial3Api
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(navController: NavController, themeViewModel: ThemeViewModel) {
    var text by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }
    val isDarkMode by themeViewModel.isDarkTheme.collectAsState()

    AppTheme(darkTheme = isDarkMode) {
        val colors = LocalAppColors.current

        Scaffold(
            topBar = {
                Header(
                    text = text,
                    active = active,
                    onQueryChange = { text = it },
                    onActiveChange = { active = it },
                    navController = navController,
                )
            },
            bottomBar = { BottomBar(navController) }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize()) {
                // In light mode, use a background image; in dark mode, use theme background color
                if (!isDarkMode) {
                    Image(
                        painter = painterResource(id = R.drawable.background),
                        contentDescription = "background",
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .matchParentSize()
                            .graphicsLayer(alpha = 0.5f)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(colors.backgroundColor)
                    )
                }

                // Main content
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item { Banner() }
                    item { ContinueRead() }
                    item { Stories(navController) }
                }
            }
        }
    }
}

@Composable
fun Banner() {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bannerhome),
            contentDescription = "Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.2f))
        )
        Image(
            painter = painterResource(id = R.drawable.poster1),
            contentDescription = "Overlay Image",
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = 16.dp)
                .height(250.dp)
                .clip(RoundedCornerShape(10.dp))
        )
    }
}

@Composable
fun ContinueRead() {
    var theme = LocalAppColors.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Continue Reading",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = theme.textPrimary

                )
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "Xem tất cả",
                    style = TextStyle(fontStyle = FontStyle.Italic),
                    color = theme.textSecondary
                )
            }
        }
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.bannerhome),
                contentDescription = "Banner",
                modifier = Modifier.fillMaxWidth()
            )
            Image(
                painter = painterResource(id = R.drawable.poster1),
                contentDescription = "Overlay Image",
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth(0.5f)
                    .height(250.dp)
            )
        }
    }
}

@Composable
fun Stories(navController: NavController) {
    val backgroundImages = listOf(
        R.drawable.banner2,
        R.drawable.banner3,
        R.drawable.banner4,
        R.drawable.bannerhome,
        R.drawable.bannerhome
    )
    val overlayImages = listOf(
        R.drawable.poster2,
        R.drawable.poster3,
        R.drawable.poster6,
        R.drawable.poster5,
        R.drawable.poster4
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Truyện ngắn")
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {},
                modifier = Modifier
                    .width(80.dp)
                    .align(Alignment.CenterVertically)
            ) {
                Text(text = "Xem tất cả")

            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { navController.navigate("bookDetail") }
        ) {
            itemsIndexed(backgroundImages) { index, backgroundImage ->
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(150.dp)
                ) {
                    Image(
                        painter = painterResource(id = backgroundImage),
                        contentDescription = "Banner",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Image(
                        painter = painterResource(id = overlayImages[index]),
                        contentDescription = "Overlay",
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .fillMaxWidth(0.5f)
                            .height(250.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewHome() {
    // For preview purposes, you can create a dummy ThemeViewModel and NavController if needed.
    // HomeScreen(navController = rememberNavController(), themeViewModel = DummyThemeViewModel())
}
