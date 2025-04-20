package com.example.storyefun.ui.components
import android.util.Log
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
import com.example.storyefun.data.repository.CommentRepository
import com.example.storyefun.ui.theme.AppColors
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentSection(theme: AppColors, bookId: String, comments: List<Comment>) {
    var newComment by remember { mutableStateOf("") }
    val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
    val commentRepository: CommentRepository = CommentRepository()
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
                    username = comment.userName,
                    date = comment.date,
                    comment = comment.content,
                    theme = theme,
                    userImageUrl = comment.userImageUrl
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

        Button(
            onClick = {
                if (newComment.isNotBlank()) {
                    // Lấy người dùng hiện tại từ FirebaseAuth
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    val currentDate = getCurrentDate() // Lấy thời gian hiện tại theo định dạng dd/MM/yyyy

                    if (currentUser != null) {
                        // Tạo đối tượng Comment
                        val newCommentData = Comment(
                            userId = currentUser.uid, // hoặc tạo ID phù hợp nếu cần
                            userName = currentUser.displayName ?: "Bạn", // Tên người dùng từ Firebase
                            date = currentDate, // Ngày hiện tại
                            content = newComment.trim(),
                            type = "text", // Loại bình luận (text, sticker, v.v.)
                            userImageUrl = currentUser.photoUrl.toString(),
                        )

                        // Gọi hàm thêm bình luận từ repository
                        CoroutineScope(Dispatchers.Main).launch {
                            try {
                                commentRepository.addComment(bookId,newCommentData) // Gọi repository để thêm comment
                                newComment = "" // Reset ô nhập bình luận sau khi gửi
                            } catch (e: Exception) {
                                // Xử lý lỗi nếu có (ví dụ: lỗi kết nối mạng hoặc Firestore gặp vấn đề)
                                Log.e("Error", "Failed to add comment: ${e.message}")
                            }
                        }
                    } else {
                        // Nếu không có người dùng đang đăng nhập, thông báo lỗi hoặc xử lý
                        Log.e("Error", "User is not authenticated")
                    }
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(
                containerColor = theme.buttonBackground,      // Màu nền của nút
                contentColor = theme.textPrimary          // Màu chữ (nếu cần)
            )
        ) {
            Text("Gửi")
        }
    }

}
fun getCurrentDate(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(Date())
}