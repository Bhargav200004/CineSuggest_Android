package com.example.cinesuggest.ui.screens.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cinesuggest.data.local.UserPreferences
import com.example.cinesuggest.domain.repository.MovieRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MovieRepository,
    private val userPreferences: UserPreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _username = MutableStateFlow("")
    val username = _username.asStateFlow()

    fun onUsernameChange(newValue: String) {
        _username.value = newValue
        // Clear error when user types
        if (_uiState.value is RegisterUiState.Error) {
            _uiState.value = RegisterUiState.Idle
        }
    }

    fun register() {
        if (_username.value.isBlank()) return

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            repository.registerUser(_username.value)
                .onSuccess { user ->
                    userPreferences.saveUserId(user.id)
                    _uiState.value = RegisterUiState.Success(user.id)
                }
                .onFailure { error ->
                    // Handle 400 "Username already registered" specifically if needed
                    _uiState.value = RegisterUiState.Error(
                        error.message ?: "Registration failed"
                    )
                }
        }
    }
}

sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    data class Success(val userId: Int) : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}