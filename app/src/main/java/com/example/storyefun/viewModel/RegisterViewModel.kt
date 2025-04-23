package com.example.storyefun.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToLogin: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(username = username, errorMessage = null)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword, errorMessage = null)
    }

    fun register() {
        val state = _uiState.value
        when {
            state.username.isBlank() || state.email.isBlank() || state.password.isBlank() || state.confirmPassword.isBlank() -> {
                _uiState.value = state.copy(errorMessage = "All fields are required")
            }
            state.password != state.confirmPassword -> {
                _uiState.value = state.copy(errorMessage = "Passwords do not match")
            }
            else -> {
                _uiState.value = state.copy(isLoading = true, errorMessage = null)
                viewModelScope.launch {
                    val result = authRepository.registerUser(state.email, state.password, state.username)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    if (result.isSuccess) {
                        _uiState.value = _uiState.value.copy(navigateToHome = true)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exceptionOrNull()?.message ?: "Register failed"
                        )
                    }
                }
            }
        }
    }

    fun navigateToLogin() {
        _uiState.value = _uiState.value.copy(navigateToLogin = true)
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigateToHome = false, navigateToLogin = false)
    }
}