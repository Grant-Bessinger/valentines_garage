package com.example.valentine_garage.ui.helper

import com.example.valentine_garage.dto.UserDto
import com.google.firebase.firestore.auth.User

sealed class AuthState {
    object Loading : AuthState()
    object Unauthenticated : AuthState()
    data class Authenticated(val user: UserDto) : AuthState()
    data class Error(val message: String) : AuthState()
}