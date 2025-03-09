package com.example.storyefun.ui.components


import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.ui.theme.AppColors

@Composable
fun CommentSection(theme: AppColors) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Bình luận (56)", fontSize = 18.sp, color = theme.textPrimary)
        CommentItem(username = "Hayao Miyazaki", date = "11/10/2024", "truyen rat hay", theme)
        CommentItem(username = "Hayao Miyazaki", date = "11/10/2024", "truyen rat hay", theme)
        CommentItem(username = "Hayao Miyazaki", date = "11/10/2024", "truyen rat hay", theme)
        CommentItem(username = "Hayao Miyazaki", date = "11/10/2024", "truyen rat hay", theme)
        CommentItem(username = "Hayao Miyazaki", date = "11/10/2024", "truyen rat hay vaf dai diai dia dai dai dai dai dai dai dai dai dai", theme)

    }
}
