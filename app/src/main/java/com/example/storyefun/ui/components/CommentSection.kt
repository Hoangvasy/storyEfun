package com.example.storyefun.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.data.models.Comment
import com.example.storyefun.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSection(theme: AppColors) {
    val comments = remember { mutableStateListOf(
        Comment("Hayao Miyazaki", "11/10/2024", "Truyện rất hay"),
        Comment("Hayao Miyazaki", "11/10/2024", "Tôi rất thích nét vẽ và cốt truyện."),
        Comment("Hayao Miyazaki", "11/10/2024", "Rất cảm động!"),
        Comment("Hayao Miyazaki", "11/10/2024", "Tôi rất thích nét vẽ và cốt truyện."),
        Comment("Hayao Miyazaki", "11/10/2024", "Rất cảm động!"),
        Comment("Hayao Miyazaki", "11/10/2024", "Tôi rất thích nét vẽ và cốt truyện."),
        Comment("Hayao Miyazaki", "11/10/2024", "Rất cảm động!"),
        Comment("Hayao Miyazaki", "11/10/2024", "Tôi rất thích nét vẽ và cốt truyện."),
        Comment("Hayao Miyazaki", "11/10/2024", "Rất cảm động!"),
        Comment("Hayao Miyazaki", "11/10/2024", "Truyện này hay lắm luôn, mong có phần tiếp theo."),
        Comment("Hayao Miyazaki", "11/10/2024", "truyen rat hay vaf dai diai dia dai dai dai dai dai dai dai dai dai")

    ) }

    var newComment by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "Bình luận (${comments.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = theme.textPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Danh sách comment
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
        ) {
            items(comments) { comment ->
                CommentItem(
                    username = comment.username,
                    date = comment.date,
                    comment = comment.content,
                    theme = theme
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập bình luận
        OutlinedTextField(
            value = newComment,
            onValueChange = { newComment = it },
            label = { Text("Viết bình luận...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            maxLines = 3,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                cursorColor = theme.textPrimary,
                focusedBorderColor = theme.textPrimary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Nút gửi
        Button(
            onClick = {
                if (newComment.isNotBlank()) {
                    comments.add(Comment("Bạn", "18/04/2025", newComment.trim()))
                    newComment = ""
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.buttonBackground,      // màu nền của nút
                contentColor = theme.textPrimary          // màu chữ (nếu cần)
            )
        ) {
            Text("Gửi")
        }

    }
}
