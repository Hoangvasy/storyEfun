package com.example.storyefun.admin.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.data.AmountOption
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DespositeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DespositeScreen()
        }
    }
}

@Composable
fun DespositeScreen() {
    val context = LocalContext.current
    val options = listOf(
        AmountOption(2000, 20),
        AmountOption(5000, 50),
        AmountOption(10000, 100),
        AmountOption(20000, 200),
        AmountOption(50000, 500),
        AmountOption(100000, 1000)
    )
    var selected by remember { mutableStateOf<AmountOption?>(null) }

    // Lấy UID người dùng từ Firebase
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    var coinBalance by remember { mutableStateOf<Int?>(null) }
    var username by remember { mutableStateOf<String?>(null) }

    // Truy vấn số dư coin của người dùng từ Firestore
    LaunchedEffect(uid) {
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    coinBalance = document.getLong("coin")?.toInt()
                    username = document.getString("username")
                }
                .addOnFailureListener {
                    coinBalance = 0 // Hoặc hiển thị lỗi nếu không lấy được dữ liệu
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chọn số tiền nạp",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(options) { option ->
                AmountBox(
                    option = option,
                    isSelected = option == selected,
                    onClick = { selected = option }
                )
            }
        }

        // Hiển thị số dư coin
        Text(
            text = "Số dư hiện tại: ${coinBalance?.toString() ?: "Đang tải..."} xu",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        Text(
            text = "Tên người dùng: ${username ?: "Đang tải..."}",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(vertical = 8.dp)
        )


        Button(
            onClick = {
                selected?.let {
                    val intent = Intent(context, OrderPayment::class.java).apply {
                        putExtra("amount", it.amount)
                        putExtra("coin", it.coin)
                    }
                    context.startActivity(intent)
                } ?: run {
                    Toast.makeText(context, "Vui lòng chọn số tiền", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = selected != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Nạp ngay")
        }
    }
}

@Composable
fun AmountBox(option: AmountOption, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .aspectRatio(2f)
            .clickable { onClick() }
            .border(
                width = 2.dp,
                color = if (isSelected) Color(0xFF4CAF50) else Color.LightGray,
                shape = MaterialTheme.shapes.medium
            )
            .background(
                color = if (isSelected) Color(0xFFE8F5E9) else Color.White,
                shape = MaterialTheme.shapes.medium
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "${option.amount}đ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = "(+${option.coin} Coin)", fontSize = 14.sp, color = Color.Gray)
        }
    }
}
