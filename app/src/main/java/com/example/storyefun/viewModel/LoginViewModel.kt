package com.example.storyefun.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyefun.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val navigateToHome: Boolean = false,
    val navigateToRegister: Boolean = false
)

class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email, errorMessage = null)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password, errorMessage = null)
    }

    fun login() {
        val state = _uiState.value
        when {
            state.email.isBlank() || state.password.isBlank() -> {
                _uiState.value = state.copy(errorMessage = "All fields are required")
            }
            else -> {
                _uiState.value = state.copy(isLoading = true, errorMessage = null)
                viewModelScope.launch {
                    val result = authRepository.loginUser(state.email, state.password)
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    if (result.isSuccess) {
                        _uiState.value = _uiState.value.copy(navigateToHome = true)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = result.exceptionOrNull()?.message ?: "Login failed"
                        )
                    }
                }
            }
        }
    }

    fun navigateToRegister() {
        _uiState.value = _uiState.value.copy(navigateToRegister = true)
    }

    fun resetNavigation() {
        _uiState.value = _uiState.value.copy(navigateToHome = false, navigateToRegister = false)
    }
}