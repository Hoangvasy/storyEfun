package com.example.storyefun.ui.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.R
import com.example.storyefun.ui.theme.AppColors

@Composable
fun CommentItem(username: String, date: String, comment: String, theme : AppColors) {
    Row(modifier = Modifier.padding(top = 8.dp)) {
        Image(
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = "User Avatar",
            modifier = Modifier.size(40.dp),
            colorFilter = ColorFilter.tint(theme.textPrimary)
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = username, fontSize = 14.sp, color = theme.textPrimary)
            Text(text = date, fontSize = 12.sp, color = theme.textSecondary)
            Text(text = comment, fontSize = 14.sp,color = theme.textPrimary)
        }
    }
}
