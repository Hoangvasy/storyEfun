package com.example.storyefun.ui.components

import coil.compose.rememberImagePainter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.ui.theme.AppColors
import com.example.storyefun.R
import com.example.storyefun.data.models.Book
import com.example.storyefun.viewModel.BookViewModel

@Composable
fun CommentItem(
    username: String,
    date: String,
    comment: String,
    theme: AppColors,
    userImageUrl: String?,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Load image using Coil, default to local resource if null
        val imagePainter = rememberImagePainter(
            data = userImageUrl,
            builder = {
                crossfade(true)
                fallback(R.drawable.ic_user) // fallback to a default image if URL is null or invalid
            }
        )

        Image(
            painter = imagePainter,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = username,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = theme.textPrimary
                )
                Text(
                    text = date,
                    fontSize = 12.sp,
                    color = theme.textSecondary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = comment,
                fontSize = 14.sp,
                color = theme.textPrimary
            )
        }
    }
}
