package com.example.storyefun.admin.ui

import android.widget.Toast
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
import androidx.navigation.NavController
import com.example.storyefun.data.AmountOption

@Composable
fun DespositeScreen(navController: NavController) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Chọn số tiền hợp lệ",
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

        Button(
            onClick = {
                selected?.let {
                    Toast.makeText(
                        context,
                        "Bạn đã chọn nạp ${it.amount}đ để nhận ${it.coin} Coin",
                        Toast.LENGTH_SHORT
                    ).show()
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
