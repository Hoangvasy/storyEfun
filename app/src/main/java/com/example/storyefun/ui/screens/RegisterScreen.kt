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
import androidx.compose.ui.graphics.Color
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

@Composable
fun RegisterScreen(navController: NavController) {


    val auth = FirebaseAuth.getInstance()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
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
                                                                                                                                                                                                                                .align(Alignment.TopStart) // Căn giữa trên c                                                                                                                                                                                                                  ùng màn hình
                                                                                                                                                                                                                                .padding(top = 60.dp, start = 20.dp) // Tạo khoảng cách với mép trên
        )
        Text(
            text = "Register",
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



            Spacer(modifier = Modifier.padding(100.dp))
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp).align(Alignment.Start)
                )
            }
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)) // Làm mờ nền

            )
            Spacer(modifier = Modifier.height(8.dp))
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
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth()
                    .background(
                        MaterialTheme
                            .colorScheme.surface.copy(alpha = 0.8f)
                    ) // Làm mờ nền
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("ConfirmPassword") },
                shape = RoundedCornerShape(15.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
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
                        username.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() -> {
                            errorMessage = "All fields are required"
                        }
                        password != confirmPassword -> {
                            errorMessage = "Sai"
                        }

                        else -> {
                            coroutineScope.launch {
                                auth.createUserWithEmailAndPassword(email, password)
                                    .addOnSuccessListener {
                                        navController.navigate("home")
                                    }
                                    .addOnFailureListener {
                                        errorMessage = it.message ?: "Register failed"
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
                Text("Register", fontSize = 25.sp)
            }


            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier.padding(top = 10.dp)

            ) {
                Text("Don't have an account? Sign up here!",  color = Color(0xFF1E90FF))
            }
        }
    }
}
