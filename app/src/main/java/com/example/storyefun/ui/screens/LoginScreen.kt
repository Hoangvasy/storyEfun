package com.example.storyefun.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.storyefun.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import com.example.storyefun.ui.theme.Blue40
import androidx.compose.material3.*

@Composable
fun LoginScreen(navController: NavController) {
    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }





    Box(
        modifier = Modifier.fillMaxSize() // Căn full màn hình
    ) {
        Image(
            painter = painterResource(id = R.drawable.screen),
            contentDescription = "Ảnh Screen",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Cắt ảnh để vừa khung
        )
        Text(
            text = "Hello,",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopStart) // Căn giữa trên cùng màn hình
                .padding(top = 60.dp, start = 20.dp) // Tạo khoảng cách với mép trên
        )
        Text(
            text = "Login",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,fontSize = 55.sp,
            modifier = Modifier

                .align(Alignment.TopStart) // Căn giữa trên cùng màn hình
                .padding(top = 100.dp, start = 20.dp) // Tạo khoảng cách với mép trên
        )
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.padding(35.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Gmail") },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) // Làm mờ nền

            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
                    .background(
                        MaterialTheme
                            .colorScheme.surface.copy(alpha = 0.8f)
                    ) // Làm mờ nền
            )
            Spacer(modifier = Modifier.height(40.dp))

            // Nút đăng nhập
            Button(
                onClick = {
                    when {
                        email.isBlank() || password.isBlank() -> {
                            errorMessage = "All fields are required"
                        }

                        else -> {
                            coroutineScope.launch {
                                auth.signInWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        navController.navigate("home")
                                    }
                                    .addOnFailureListener {
                                        errorMessage = it.message ?: "Login failed"
                                    }
                            }
                        }
                    }
                },

                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E90FF)) // Đúng cho Material 3
            ) {
                Text("LOGIN", fontSize = 25.sp)
            }


            TextButton(
                onClick = { navController.navigate("register") },
                modifier = Modifier.padding(top = 10.dp)

            ) {
                Text("Don't have an account? Sign up here!",  color = Color(0xFF1E90FF))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Đẩy Row xuống dưới
                .padding(bottom = 100.dp), // Khoảng cách với mép dưới
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = "https://png.pngtree.com/png-clipart/20180515/ourmid/pngtree-facebook-logo-facebook-icon-png-image_3566127.png",
                    contentDescription = "Facebook Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Box(
                modifier = Modifier
                    .size(100.dp)
                    .padding(10.dp)
            ) {
                AsyncImage(
                    model = "https://upload.wikimedia.org/wikipedia/commons/0/09/IOS_Google_icon.png",
                    contentDescription = "Google Logo",
                    modifier = Modifier.fillMaxSize()
                )
            }


        }
    }
}
