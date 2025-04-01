import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

@Composable
fun UploadScreen(navController: NavController) {
    var title by remember { mutableStateOf("") }
    var authorName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<List<String>>(emptyList()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val categories = listOf("Tiểu thuyết", "Truyện ngắn", "Kinh dị", "Hành động", "Lãng mạn")

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    // Hàm lưu vào Firebase
    val saveData = { title: String, author: String, description: String, categories: List<String>, imageUri: Uri? ->
        isLoading = true
        saveBookToFirebase(title, author, description, categories, imageUri, context) { isSuccess ->
            isLoading = false
            if (isSuccess) {
                Toast.makeText(context, "Dữ liệu đã được lưu thành công", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Lỗi khi lưu dữ liệu", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Cập nhật truyện", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))

        // Nhập thông tin
        TextFieldWithLabel("Tên truyện", "Nhập tên truyện", value = title, onValueChange = { title = it })
        TextFieldWithLabel("Tên tác giả", "Nhập tên tác giả", value = authorName, onValueChange = { authorName = it })
        TextFieldWithLabel("Mô tả", "Nhập mô tả", value = description, onValueChange = { description = it })

        // Chọn thể loại
        CategoryButtonGroup(
            selectedCategory = selectedCategory,
            onCategorySelected = { category ->
                selectedCategory = if (selectedCategory.contains(category)) {
                    selectedCategory - category
                } else {
                    selectedCategory + category
                }
            },
            categories = categories
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Chọn ảnh
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                .background(Color(0xFFF2F2F2))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(imageVector = Icons.Outlined.Person, contentDescription = "Upload", tint = Color.Blue, modifier = Modifier.size(50.dp))
                    Text("Upload your files here", color = Color.Gray)
                    TextButton(onClick = { imagePickerLauncher.launch("image/*") }) {
                        Text("Browse", color = Color.Blue)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Nút lưu dữ liệu
        Button(
            onClick = { saveData(title, authorName, description, selectedCategory, imageUri) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue),
            enabled = !isLoading // Disable button while saving
        ) {
            Text(text = if (isLoading) "Đang lưu..." else "Lưu lại", color = Color.White, fontSize = 16.sp)
        }
    }
}

// TextField chung
@Composable
fun TextFieldWithLabel(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        TextField(value = value, onValueChange = onValueChange, placeholder = { Text(text = placeholder) }, modifier = Modifier.fillMaxWidth())
    }
}

// Nhóm nút chọn thể loại
@Composable
fun CategoryButtonGroup(selectedCategory: List<String>, onCategorySelected: (String) -> Unit, categories: List<String>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Phân loại", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        LazyRow(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
            items(categories) { category ->
                Button(
                    onClick = { onCategorySelected(category) },
                    colors = ButtonDefaults.buttonColors(containerColor = if (selectedCategory.contains(category)) Color.Blue else Color.Gray),
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(text = category, color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

// Hàm lưu dữ liệu vào Firestore & Storage
fun saveBookToFirebase(
    title: String,
    author: String,
    description: String,
    categories: List<String>,
    imageUri: Uri?,
    context: Context,
    onComplete: (Boolean) -> Unit
) {
    val db = FirebaseFirestore.getInstance()

    // Dữ liệu cần lưu
    val bookData = hashMapOf(
        "name" to title,
        "author" to author,
        "description" to description,
        "categories" to categories,
        "likes" to 0,
        "views" to 0,
        "follows" to 0
    )

    // Kiểm tra nếu có ảnh được chọn
    if (imageUri != null) {
        // Lưu ảnh vào Firebase Storage
        val storageRef = FirebaseStorage.getInstance().reference.child("book_images/${UUID.randomUUID()}.jpg")

        // Tải lên tệp ảnh
        storageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Lấy URL ảnh đã tải lên thành công
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    // Lưu URL ảnh vào dữ liệu
                    bookData["imageUrl"] = downloadUri.toString()

                    // Tạo document ID ngẫu nhiên
                    val documentId = UUID.randomUUID().toString()

                    // Lưu dữ liệu vào Firestore
                    db.collection("books").document(documentId).set(bookData)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Dữ liệu đã được lưu thành công")
                            onComplete(true)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Lỗi khi lưu dữ liệu: ${e.message}")
                            onComplete(false)
                        }
                }.addOnFailureListener { e ->
                    Log.e("Firestore", "Lỗi khi lấy URL ảnh: ${e.message}")
                    onComplete(false)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi lưu ảnh: ${e.message}")
                onComplete(false)
            }
    } else {
        // Nếu không có ảnh, chỉ lưu thông tin vào Firestore
        val documentId = UUID.randomUUID().toString()
        db.collection("books").document(documentId).set(bookData)
            .addOnSuccessListener {
                Log.d("Firestore", "Dữ liệu đã được lưu thành công")
                onComplete(true)
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Lỗi khi lưu dữ liệu: ${e.message}")
                onComplete(false)
            }
    }
}
