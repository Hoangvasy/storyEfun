import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun UploadScreen(navController: NavController) {
    var isFull by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("") }
    val categories = listOf("Tiểu thuyết", "Truyện ngắn", "Kinh dị", "Hành động", "Lãng mạn")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Cập nhật tiểu thuyết",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.fillMaxWidth()) {
            TextFieldWithLabel("Tiêu đề", "Nhập tiêu đề")
            CategoryButtonGroup(
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = it },
                categories = categories
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Đã full")
            Spacer(modifier = Modifier.weight(1f))
            Switch(checked = isFull, onCheckedChange = { isFull = it })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Upload",
                    tint = Color.Blue,
                    modifier = Modifier.size(50.dp)
                )
                Text("Upload your files here", color = Color.Gray)
                TextButton(onClick = { /* Chọn file */ }) {
                    Text("Browse", color = Color.Blue)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Xử lý lưu */ },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue)
        ) {
            Text(text = "Lưu lại", color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun TextFieldWithLabel(label: String, placeholder: String) {
    var text by remember { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(text = placeholder) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CategoryButtonGroup(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    categories: List<String>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Phân loại", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        val chunkedCategories = categories.chunked(3)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            chunkedCategories.forEach { rowCategories ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowCategories.forEach { category ->
                        Button(
                            onClick = { onCategorySelected(category) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCategory == category) Color.Blue else Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = category, color = Color.White, fontSize = 12.sp)
                        }
                    }
                    if (rowCategories.size < 3) {
                        repeat(3 - rowCategories.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
