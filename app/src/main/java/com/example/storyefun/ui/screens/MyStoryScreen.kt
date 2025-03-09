package com.example.storyefun.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.storyefun.R

@Composable
fun MyStoryScreen(navController: NavController) {
    val stories = listOf(
        Story("One Piece", "Hành động / Phiêu lưu", "1", "5K", "1.2K", "500"),
        Story("Naruto", "Hành động / Phiêu lưu", "2", "10K", "2K", "700"),
        Story("Doraemon", "Hài hước / Phiêu lưu", "3", "8K", "1.5K", "600")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(text = "Truyện của tôi", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        LazyColumn(modifier = Modifier.padding(16.dp)) {
            items(stories) { story ->
                StoryCard(story)
                Spacer(modifier = Modifier.height(12.dp)) // Khoảng cách giữa các card
            }
        }
    }
}

@Composable
fun StoryCard(story: Story) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Black, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.poster1), // Thay bằng ID ảnh thực tế
                contentDescription = "Hình ảnh truyện",
                modifier = Modifier
                    .size(150.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = story.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = story.genre, fontSize = 14.sp)
                Text(text = "Chương: ${story.chapter}", fontSize = 14.sp)
                Text(text = "Lượt đọc: ${story.views}", fontSize = 14.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Like", tint = Color.Red)
                    Text(text = story.likes, fontSize = 14.sp)
                    Icon(imageVector = Icons.Default.Face, contentDescription = "Bình luận", tint = Color.Gray)
                    Text(text = story.comments, fontSize = 14.sp)
                }
            }
        }
    }
}

data class Story(
    val title: String,
    val genre: String,
    val chapter: String,
    val views: String,
    val likes: String,
    val comments: String
)
