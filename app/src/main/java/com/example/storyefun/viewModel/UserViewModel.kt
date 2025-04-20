package com.example.storyefun.viewModel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.storyefun.data.repository.BookRepository
import com.example.storyefun.data.repository.CloudnaryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel() : ViewModel() {
    private val cloudnaryRepository: CloudnaryRepository = CloudnaryRepository()
    private val auth = FirebaseAuth.getInstance()
    private val bookRepository: BookRepository = BookRepository()
    // Update user's avatar (photoUrl) using Cloudinary
    fun updateAvatar(
        imageUri: Uri,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                cloudnaryRepository.uploadToCloudinary(
                    uri = imageUri,
                    folder = "user_avatars",
                    onSuccess = { secureUrl ->
                        // Launch coroutine here to call suspend function
                        viewModelScope.launch {
                            try {
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse(secureUrl))
                                    .build()
                                auth.currentUser?.updateProfile(profileUpdates)?.await()
                                onSuccess()
                            } catch (e: Exception) {
                                onFailure(e)
                            }
                        }
                    },
                    onFailure = { e ->
                        onFailure(e)
                    }
                )
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // Update user's display name
    fun updateUsername(newName: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)?.await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    // Update user's password
    fun updatePassword(newPassword: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        viewModelScope.launch {
            try {
                auth.currentUser?.updatePassword(newPassword)?.await()
                onSuccess()
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }

    suspend fun isLikedBook(bookId: String): Boolean {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            val snapshot = userRef.get().await()
            val likedBooks = snapshot.get("likedBooks") as? List<*>
            likedBooks?.contains(bookId) == true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun isFollowedBook(bookId: String): Boolean {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return false
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            val snapshot = userRef.get().await()
            val followedBooks = snapshot.get("followedBooks") as? List<*>
            followedBooks?.contains(bookId) == true

        } catch (e: Exception) {
            false
        }
    }

    suspend fun likingBook(bookId: String) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.update("likedBooks", FieldValue.arrayUnion(bookId)).await()
            bookRepository.valueIncrease("like", bookId)

        } catch (e: Exception) {
            Log.e("likingBook", "Error liking book: ${e.message}")
        }
    }

    suspend fun followingBook(bookId: String) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)
            userRef.update("followedBooks", FieldValue.arrayUnion(bookId)).await()
            bookRepository.valueIncrease("follow", bookId)
        } catch (e: Exception) {
            Log.e("followingBook", "Error following book: ${e.message}")
        }
    }

    suspend fun unlikingBook(bookId: String) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            userRef.update("likedBooks", FieldValue.arrayRemove(bookId)).await()
            bookRepository.valueDecrease("like", bookId)
        } catch (e: Exception) {
            Log.e("unlikingBook", "Error removing like: ${e.message}")
        }
    }

    suspend fun unfollowingBook(bookId: String) {
        try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
            val userRef = FirebaseFirestore.getInstance().collection("users").document(userId)

            userRef.update("followedBooks", FieldValue.arrayRemove(bookId)).await()
            bookRepository.valueDecrease("follow", bookId)

          //  Log.d("unfollowingBook", "Successfully removed follow from $bookId")
        } catch (e: Exception) {
            Log.e("unfollowingBook", "Error removing follow: ${e.message}")
        }
    }

    suspend fun buyChapter(bookId: String, volumeOrder: String, chapterId: String)
    {

    }


    fun getBalance(): Long {
        val userId = auth.uid ?: return 0L

        return try {
            val snapshot = Firebase.firestore.collection("users").document(userId).get().result
            snapshot.getLong("coin") ?: 0L
        } catch (e: Exception) {
            0L // hoặc xử lý lỗi theo cách bạn muốn
        }
    }

}