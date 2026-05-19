package com.example.valentine_garage.ui.screens.authentication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.helper.LoginUiState
import com.example.valentine_garage.ui.viewModels.AuthViewModel

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var resetStatusMessage by remember { mutableStateOf<String?>(null) }
    var isResetLoading by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()

    LaunchedEffect(loginState) {
        if (loginState is LoginUiState.Success) {
            onNavigateToHome()
            viewModel.resetLoginState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Valentine Garage",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Sign in to continue",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Hide" else "Show")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { showForgotDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Forgot Password?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (loginState is LoginUiState.Error) {
            Text(
                text = (loginState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = { viewModel.login(email, password) },
            enabled = loginState !is LoginUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (loginState is LoginUiState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Sign In")
            }
        }

        if (showForgotDialog) {

            AlertDialog(
                onDismissRequest = {
                    showForgotDialog = false
                    resetStatusMessage = null
                },

                title = {
                    Text("Reset Password")
                },

                text = {

                    Column {

                        Text(
                            "Enter your email address and we'll send you a password reset link."
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = forgotEmail,
                            onValueChange = { forgotEmail = it },
                            label = { Text("Email") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (resetStatusMessage != null) {

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = resetStatusMessage!!,
                                color = if (
                                    resetStatusMessage!!.contains(
                                        "sent",
                                        ignoreCase = true
                                    )
                                ) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.error
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },

                confirmButton = {

                    Button(
                        onClick = {

                            if (forgotEmail.isBlank()) {
                                resetStatusMessage = "Please enter your email"
                                return@Button
                            }

                            isResetLoading = true

                            viewModel.sendPasswordResetEmail(forgotEmail) { result ->

                                isResetLoading = false

                                when (result) {

                                    is FirebaseResult.Success -> {
                                        resetStatusMessage =
                                            "Password reset email sent successfully."
                                    }

                                    is FirebaseResult.Failure -> {
                                        resetStatusMessage =
                                            result.exception.message
                                                ?: "Failed to send reset email"
                                    }

                                    else -> Unit
                                }
                            }
                        },
                        enabled = !isResetLoading
                    ) {

                        if (isResetLoading) {

                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )

                        } else {

                            Text("Send")
                        }
                    }
                },

                dismissButton = {

                    TextButton(
                        onClick = {
                            showForgotDialog = false
                            resetStatusMessage = null
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}