package com.example.storyefun.viewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.repository.CloudnaryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UserViewModel() : ViewModel() {
    private val cloudnaryRepository: CloudnaryRepository = CloudnaryRepository()
    private val auth = FirebaseAuth.getInstance()

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
    fun likingBook(bookId : String)
    {

    }
    fun followingBook(bookId: String)
    {

    }
    fun buyChapter(bookId: String, volumeOrder: String, chapterId: String)
    {

    }
}