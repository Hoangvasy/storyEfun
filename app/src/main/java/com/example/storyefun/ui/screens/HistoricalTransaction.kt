package com.example.storyefun.admin.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.storyefun.data.models.Transactions
import com.example.storyefun.data.repository.TransactionRepository
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.viewModel.TransactionViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoricalTransaction(navController: NavController) {
    val viewModel: TransactionViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val uid = FirebaseAuth.getInstance().currentUser?.uid
                if (uid == null) {
                    Log.e("HistoricalTransaction", "User not authenticated")
                }
                return TransactionViewModel(TransactionRepository(), uid ?: "") as T
            }
        }
    )
    val theme = LocalAppColors.current
    val transactions by viewModel.transactions.collectAsState()
    val transactionStatus by viewModel.transactionStatus.collectAsState()

    LaunchedEffect(transactions) {
        Log.d("HistoricalTransaction", "Transactions updated: size=${transactions.size}, data=$transactions")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Lịch sử giao dịch",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        transactionStatus?.let {
            Text(
                text = it,
                color = Color(0xFFFF5722),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        if (transactions.isEmpty()) {
            Text(
                text = "Không có giao dịch nào",
                color = Color(0xFF666666),
                fontSize = 16.sp,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(transactions, key = { "${it.uid}_${it.time}" }) { transaction ->
                    Log.d("HistoricalTransaction", "Rendering TransactionItem: $transaction")
                    TransactionItem(transaction)
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                Log.d("HistoricalTransaction", "Refreshing transactions")
                viewModel.loadTransactions()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = theme.buttonOrange),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text("Làm mới", color = Color.Black)
        }
    }
    LaunchedEffect(Unit) {
        Log.d("HistoricalTransaction", "Loading transactions on start")
        viewModel.loadTransactions()
    }
}

@Composable
fun TransactionItem(transaction: Transactions) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Số tiền: ${String.format("%.0f", transaction.money)} VNĐ",
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = "Số coin: ${transaction.coin} Coin",
                fontSize = 16.sp,
                color = Color(0xFF333333)
            )
            Text(
                text = "Thời gian: ${
                    SimpleDateFormat("HH:mm  dd/MM/yyyy ", Locale.getDefault())
                        .format(Date(transaction.time))
                }",
                fontSize = 14.sp,
                color = Color(0xFF666666)
            )
        }
    }
}