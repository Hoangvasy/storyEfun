package com.example.storyefun.admin.ui

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.storyefun.navigation.Screen
import com.example.storyefun.ui.theme.LocalAppColors
import com.example.storyefun.zaloPay.Api.CreateOrder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.json.JSONObject
import vn.zalopay.sdk.Environment
import vn.zalopay.sdk.ZaloPayError
import vn.zalopay.sdk.ZaloPaySDK
import vn.zalopay.sdk.listeners.PayOrderListener

class OrderPayment : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        ZaloPaySDK.init(2553, Environment.SANDBOX)

        val amount = intent.getIntExtra("amount", 0)
        val coin = intent.getIntExtra("coin", 0)

        setContent {
            OrderPaymentScreen(amount, coin)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let {
            ZaloPaySDK.getInstance().onResult(it)
        }
    }
}

@Composable
fun OrderPaymentScreen(amount: Int, coin: Int) {
    val theme = LocalAppColors.current
    val context = LocalContext.current
    val amountFormatted = String.format("%.0f", amount.toDouble())
    var paymentStatus by remember { mutableStateOf<String?>(null) }

    var userName by remember { mutableStateOf<String?>(null) }
    var coinBalance by remember { mutableStateOf<Int?>(null) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        if (uid != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    userName = document.getString("username") ?: "Người dùng không xác định"
                    coinBalance = document.getLong("coin")?.toInt()
                }
                .addOnFailureListener {
                    userName = "Không thể lấy thông tin người dùng"
                    coinBalance = 0
                }
        }
    }

    if (userName == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color(0xFF6B48FF))
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header với gradient cam
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFFFB300), Color(0xFFFFA000))
                        )
                    )
                    .padding(vertical = 12.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    IconButton(onClick = {
                        (context as? ComponentActivity)?.onBackPressedDispatcher?.onBackPressed()
                    }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = theme.textPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Đơn hàng",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Nội dung chính
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = 16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Thông tin nạp tiền trong Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F0F0)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Số tiền nạp:",
                                fontSize = 20.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                "$amountFormatted VNĐ",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Số coin:",
                                fontSize = 20.sp,
                                color = Color(0xFF333333)
                            )
                            Text(
                                "$coin Coin",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }

                // Số dư hiện tại trong Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .shadow(2.dp, RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F0F0)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Số dư hiện tại:",
                            fontSize = 20.sp,
                            color = Color(0xFF333333)
                        )
                        Text(
                            "$coinBalance Coin",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF5722)
                        )
                    }
                }

                paymentStatus?.let {
                    Text(
                        text = it,
                        color = Color(0xFF6B48FF),
                        fontSize = 16.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                    )
                }
            }

            // Nút thanh toán được đẩy lên trên
            Button(
                onClick = {
                    val orderApi = CreateOrder()
                    try {
                        val data: JSONObject = orderApi.createOrder(amountFormatted)
                        if (data.getString("return_code") == "1") {
                            val token = data.getString("zp_trans_token")
                            ZaloPaySDK.getInstance().payOrder(
                                context as ComponentActivity,
                                token,
                                "demozpdk://app",
                                object : PayOrderListener {
                                    override fun onPaymentSucceeded(s: String?, s1: String?, s2: String?) {
                                        paymentStatus = "Thanh toán thành công"
                                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                                        if (uid != null && coinBalance != null) {
                                            val newCoin = coinBalance!! + coin
                                            FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(uid)
                                                .update("coin", newCoin)
                                                .addOnSuccessListener {
                                                    Log.d("OrderPayment", "Cập nhật coin thành công: $newCoin")
                                                    context.startActivity(Intent(context, Screen.PaymentNotification::class.java).apply {
                                                        putExtra("result", "Giao dịch thành công")
                                                    })
                                                }
                                                .addOnFailureListener {
                                                    context.startActivity(Intent(context, Screen.PaymentNotification::class.java).apply {
                                                        putExtra("result", "Cập nhật coin thất bại")
                                                    })
                                                }
                                        } else {
                                            paymentStatus = "Lỗi khi lấy thông tin người dùng"
                                            context.startActivity(Intent(context, Screen.PaymentNotification::class.java).apply {
                                                putExtra("result", "Lỗi khi lấy thông tin người dùng")
                                            })
                                        }
                                    }

                                    override fun onPaymentCanceled(s: String?, s1: String?) {
                                        paymentStatus = "Thanh toán thất bại. Vui lòng thử lại."
                                        context.startActivity(Intent(context, Screen.PaymentNotification::class.java).apply {
                                            putExtra("result", "Thanh toán thất bại.")
                                        })
                                    }

                                    override fun onPaymentError(zaloPayError: ZaloPayError?, s: String?, s1: String?) {
                                        paymentStatus = "Lỗi thanh toán"
                                        context.startActivity(Intent(context, Screen.PaymentNotification::class.java).apply {
                                            putExtra("result", "Lỗi thanh toán")
                                        })
                                    }
                                }
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        paymentStatus = "Lỗi tạo đơn hàng"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp) // Giảm padding vertical để đẩy lên
                    .height(64.dp)
                    .shadow(5.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFFFFB300), Color(0xFFFFA000))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Thanh toán bằng Zalo Pay",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
    }
}