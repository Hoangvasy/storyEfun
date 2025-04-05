package com.example.storyefun.admin.ui

import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.*
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import com.example.storyefun.data.Book
import com.example.storyefun.data.BookRepository
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun EditBook(navController: NavController, bookId: String?) {
    var book by remember { mutableStateOf<Book?>(null) }
    var name by remember { mutableStateOf(TextFieldValue("")) }
    var author by remember { mutableStateOf(TextFieldValue("")) }
    var description by remember { mutableStateOf(TextFieldValue("")) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope() // Lấy scope coroutine
    val context = LocalContext.current // Lấy context cho Toast

    // Lấy thông tin sách từ Repository
    LaunchedEffect(bookId) {
        if (bookId != null) {
            try {
                val books = BookRepository().getBooks() // Giả sử bạn đã có hàm getBooks() để lấy tất cả sách
                book = books.find { it.id == bookId } // Tìm sách theo bookId
                book?.let {
                    name = TextFieldValue(it.name)
                    author = TextFieldValue(it.author)
                    description = TextFieldValue(it.description)
                }
                isLoading = false
            } catch (e: Exception) {
                errorMessage = "Error loading book details"
                isLoading = false
            }
        }
    }

    // Nếu đang tải hoặc có lỗi, hiển thị thông báo
    if (isLoading) {
        Text("Loading book details...", style = MaterialTheme.typography.bodyLarge)
    } else if (errorMessage != null) {
        Text(errorMessage!!, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
    } else if (book == null) {
        Text("Book not found.", style = MaterialTheme.typography.bodyLarge)
    } else {
        // Màn hình chỉnh sửa sách
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Edit Book", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            // Tên sách
            Text("Name")
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter book name") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tác giả
            Text("Author")
            TextField(
                value = author,
                onValueChange = { author = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter author name") }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Mô tả
            Text("Description")
            TextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter book description") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Nút lưu thay đổi
            Button(
                onClick = {
                    if (book != null) {
                        // Tạo bản sao của sách với các thay đổi
                        val updatedBook = book?.copy(
                            name = name.text,
                            author = author.text,
                            description = description.text
                        )
                        updatedBook?.let { updatedBook ->
                            coroutineScope.launch {
                                // Cập nhật vào Firebase trong coroutine
                                val isUpdated = BookRepository().updateBook(updatedBook)

                                // Hiển thị Toast thông báo thành công hoặc thất bại
                                if (isUpdated) {
                                    Toast.makeText(
                                        context,
                                        "Book updated successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    navController.popBackStack() // Quay lại màn hình trước đó sau khi cập nhật

                                } else {
                                    Toast.makeText(
                                        context,
                                        "Error updating book",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Changes")
            }
        }
    }
}
