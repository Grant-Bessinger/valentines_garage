package com.example.valentine_garage.ui.helper

import com.example.valentine_garage.ui.enums.UserRole

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val role: UserRole) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}