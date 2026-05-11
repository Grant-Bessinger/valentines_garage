package com.example.valentine_garage.ui.viewModels

import com.example.valentine_garage.dto.UserDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.helper.AuthState
import com.example.valentine_garage.ui.repositories.AuthRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentine_garage.ui.helper.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState.asStateFlow()

    val authState: StateFlow<AuthState> = MutableStateFlow<AuthState>(AuthState.Loading).also { flow ->
        viewModelScope.launch {
            repo.authState.collect { state ->
                flow.value = state
            }
        }
    }.asStateFlow()

    val allUsers: StateFlow<List<UserDto>> = repo.getAllUsersLocal()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _loginState.value = LoginUiState.Error("Email and password cannot be empty")
            return
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            when (val result = repo.login(email, password)) {
                is FirebaseResult.Success -> {
                    _loginState.value = LoginUiState.Success(result.data.toRole())
                }
                is FirebaseResult.Failure -> {
                    _loginState.value = LoginUiState.Error(result.exception.message ?: "Login failed")
                }
                else -> Unit
            }
        }
    }


    fun logout() {
        repo.logout()
        _loginState.value = LoginUiState.Idle
    }

    fun seedUsers() {
        viewModelScope.launch { repo.seedDefaultUsers() }
    }

    fun resetLoginState() {
        _loginState.value = LoginUiState.Idle
    }

    // --- Local User Methods (from former UserViewModel) ---

    fun addUser(user: UserDto) {
        viewModelScope.launch {
            repo.insertUserLocal(user)
        }
    }

    fun deleteUser(user: UserDto) {
        viewModelScope.launch {
            repo.deleteUserLocal(user)
        }
    }
}

