package com.example.storyefun.admin.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.storyefun.ui.theme.LocalAppColors

@Composable
fun CategoryBook(
    selectedCategory: List<String>,
    onCategorySelected: (String) -> Unit
) {
    // Danh sách các thể loại sách
    val categories = listOf("Tiểu thuyết", "Truyện ngắn", "Kinh dị", "Hành động", "Lãng mạn", "Hài hước- vui")

    // Hiển thị các button để người dùng chọn thể loại
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Chia danh sách categories thành các nhóm con, mỗi nhóm con có tối đa 2 thể loại
        categories.chunked(2).forEach { categoryRow ->  // Chia thành nhóm 2 thể loại
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Hiển thị button cho từng thể loại trong nhóm
                categoryRow.forEach { category ->
                    val isSelected = selectedCategory.contains(category)
                    CategoryButton(
                        category = category,
                        isSelected = isSelected,
                        onClick = {
                            onCategorySelected(category)
                        },
                        modifier = Modifier
                            .padding(4.dp) // Sử dụng padding để tạo khoảng cách giữa các button
                            .wrapContentWidth() // Đảm bảo button có chiều rộng vừa đủ với nội dung
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryButton(
    category: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Lấy màu sắc từ LocalAppColors để áp dụng cho button
    val appColors = LocalAppColors.current

    // Nếu button được chọn, dùng màu từ theme (nền button và chữ sẽ thay đổi khi chọn)
    val backgroundColor = if (isSelected) appColors.buttonBackground else Color(0xFFCCC2DC) // Màu nền khi chọn, màu nền khi không chọn
    val contentColor = if (isSelected) appColors.buttonText else Color.Black // Màu chữ khi chọn, màu chữ khi không chọn

    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor, // Sử dụng containerColor thay cho backgroundColor
            contentColor = contentColor // Sử dụng contentColor thay cho textColor
        )
    ) {
        Text(text = category)
    }
}
