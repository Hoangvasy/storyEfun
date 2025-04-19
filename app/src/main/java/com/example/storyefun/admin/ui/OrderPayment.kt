package com.example.storyefun.admin.ui

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

        // Cho phép gọi mạng trên main thread (chỉ dùng khi test)
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder().permitAll().build()
        )

        // Khởi tạo ZaloPay SDK
        ZaloPaySDK.init(2553, Environment.SANDBOX)

        val amount = intent.getIntExtra("amount", 0)  // Lấy số tiền từ Intent
        val coin = intent.getIntExtra("coin", 0)      // Lấy số coin từ Intent

        setContent {
            OrderPaymentScreen(amount, coin)  // Truyền dữ liệu vào màn hình
        }
    }

    // Sử dụng phương thức onNewIntent từ Activity thay vì ComponentActivity
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.let {
            ZaloPaySDK.getInstance().onResult(it)
        }
    }
}

@Composable
fun OrderPaymentScreen(amount: Int, coin: Int) {
    val context = LocalContext.current
    val amountFormatted = String.format("%.0f", amount.toDouble())  // Chuyển số tiền sang định dạng chuỗi
    var paymentStatus by remember { mutableStateOf<String?>(null) }

    // Dữ liệu người dùng
    var userName by remember { mutableStateOf<String?>(null) }
    var coinBalance by remember { mutableStateOf<Int?>(null) }
    val uid = FirebaseAuth.getInstance().currentUser?.uid

    // Chạy effect để lấy thông tin người dùng từ Firebase
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
                    coinBalance = 0 // hoặc hiển thị lỗi nếu không thể lấy thông tin người dùng
                }
        }
    }

    // Đợi cho đến khi `userName` có giá trị không null
    if (userName == null) {
        // Có thể hiển thị một loading spinner hoặc một thông báo trong khi đang tải thông tin
        CircularProgressIndicator()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Đơn hàng", fontSize = 24.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(32.dp))

            // Hiển thị tên người dùng
            Text("Chào, ${userName}", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(32.dp))
            Text("Coin, ${coinBalance}", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(32.dp))
            Text("Số tiền nạp: $amountFormatted VNĐ", fontSize = 20.sp)

            Spacer(modifier = Modifier.height(8.dp))
            Text("Số coin: $coin Coin", fontSize = 18.sp)

            Spacer(modifier = Modifier.height(16.dp))
            paymentStatus?.let {
                Text(text = it, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
            }

            Button(onClick = {
                val orderApi = CreateOrder()
                try {
                    val data: JSONObject = orderApi.createOrder(amountFormatted)  // Gửi số tiền đã chọn
                    if (data.getString("return_code") == "1") {
                        val token = data.getString("zp_trans_token")
                        ZaloPaySDK.getInstance().payOrder(
                            context as ComponentActivity,
                            token,
                            "demozpdk://app",  // URL callback
                            object : PayOrderListener {

                                override fun onPaymentSucceeded(s: String?, s1: String?, s2: String?) {
                                    paymentStatus = "Thanh toán thành công"

                                    // Cập nhật số dư coin vào Firestore
                                    val uid = FirebaseAuth.getInstance().currentUser?.uid
                                    if (uid != null && coinBalance != null) {
                                        val newCoin = coinBalance!! + coin

                                        // Cập nhật Firestore
                                        FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(uid)
                                            .update("coin", newCoin)
                                            .addOnSuccessListener {
                                                Log.d("OrderPayment", "Cập nhật coin thành công: $newCoin")
                                                // Sau khi cập nhật Firestore thành công, chuyển đến màn hình thông báo
                                                context.startActivity(Intent(context, PaymentNotification::class.java).apply {
                                                    putExtra("result", "Thanh toán thành công")
                                                })
                                            }
                                            .addOnFailureListener {
                                                // Nếu có lỗi trong việc cập nhật Firestore, hiển thị thông báo lỗi
                                                context.startActivity(Intent(context, PaymentNotification::class.java).apply {
                                                    putExtra("result", "Cập nhật coin thất bại")
                                                })
                                            }
                                    } else {
                                        // Trường hợp không lấy được uid hoặc coinBalance
                                        paymentStatus = "Lỗi khi lấy thông tin người dùng"
                                        context.startActivity(Intent(context, PaymentNotification::class.java).apply {
                                            putExtra("result", "Lỗi khi lấy thông tin người dùng")
                                        })
                                    }
                                }

                                override fun onPaymentCanceled(s: String?, s1: String?) {
                                    paymentStatus = "Hủy thanh toán"
                                    context.startActivity(Intent(context, PaymentNotification::class.java).apply {
                                        putExtra("result", "Hủy thanh toán")
                                    })
                                }

                                override fun onPaymentError(zaloPayError: ZaloPayError?, s: String?, s1: String?) {
                                    paymentStatus = "Lỗi thanh toán"
                                    context.startActivity(Intent(context, PaymentNotification::class.java).apply {
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
            }) {
                Text("Thanh toán bằng Zalo Pay")
            }
        }
    }
}
