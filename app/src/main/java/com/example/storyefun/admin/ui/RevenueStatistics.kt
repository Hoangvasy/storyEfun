package com.example.storyefun.admin.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.storyefun.data.repository.RevenueRepository
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.RevenueViewModel
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun RevenueStatisticsScreen(navController: NavController) {
    val viewModel: RevenueViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RevenueViewModel(RevenueRepository()) as T
            }
        }
    )
    val theme = LocalAppColors.current
    val monthlyRevenue by viewModel.monthlyRevenue.collectAsState()
    val status by viewModel.status.collectAsState()
    val selectedYear by viewModel.selectedYear.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()

    // Danh sách năm (2024, 2025)
    val years = listOf(2024, 2025)
    val months = listOf(
        "Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6",
        "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Doanh thu",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Dropdown chọn năm
        var yearExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = selectedYear.toString(),
                onValueChange = {},
                label = { Text("Năm") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                trailingIcon = {
                    IconButton(onClick = { yearExpanded = true }) {
                        Icon(
                             Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = yearExpanded,
                onDismissRequest = { yearExpanded = false }
            ) {
                years.forEach { year ->
                    DropdownMenuItem(
                        text = { Text(year.toString()) },
                        onClick = {
                            viewModel.setYear(year)
                            yearExpanded = false
                        }
                    )
                }
            }
        }

        // Dropdown chọn tháng
        var monthExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedTextField(
                value = months[selectedMonth - 1],
                onValueChange = {},
                label = { Text("Tháng") },
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                trailingIcon = {
                    IconButton(onClick = { monthExpanded = true }) {
                        Icon(
                           Icons.Default.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                }
            )
            DropdownMenu(
                expanded = monthExpanded,
                onDismissRequest = { monthExpanded = false }
            ) {
                months.forEachIndexed { index, month ->
                    DropdownMenuItem(
                        text = { Text(month) },
                        onClick = {
                            viewModel.setMonth(index + 1)
                            monthExpanded = false
                        }
                    )
                }
            }
        }

        // Nút Xem
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                Log.d("RevenueStatistics", "Loading revenue for $selectedYear-$selectedMonth")
                viewModel.loadRevenue()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.buttonOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.linearGradient(colors = listOf(Color(0xFFFFB300), Color(0xFFFFA000)))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Xem",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Trạng thái
        Spacer(modifier = Modifier.height(16.dp))
        status?.let {
            Text(
                text = it,
                color = Color(0xFFFF5722),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Card doanh thu
        monthlyRevenue?.let { revenue ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${months[selectedMonth - 1]} $selectedYear:",
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                    Text(
                        text = if (revenue > 0) NumberFormat.getNumberInstance(Locale("vi", "VN")).format(revenue) + " VNĐ" else "0 VNĐ",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF5722)
                    )
                }
            }
        } ?: run {
            if (status == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFB300))
                }
            }
        }
    }
}