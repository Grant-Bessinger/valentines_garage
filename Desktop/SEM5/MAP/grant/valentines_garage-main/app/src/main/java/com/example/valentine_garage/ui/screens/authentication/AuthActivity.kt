package com.example.valentine_garage.ui.screens.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.ui.helper.AuthState
import com.example.valentine_garage.ui.screens.home.HomeActivity
import com.example.valentine_garage.ui.theme.ValentineGarageTheme
import com.example.valentine_garage.ui.viewModels.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val viewModel: AuthViewModel = hiltViewModel()

            ValentineGarageTheme {
                LoginScreen(
                    onNavigateToHome = { navigateToMain() },
                    viewModel = viewModel
                )
            }
        }
    }

    private fun navigateToMain() {
        startActivity(
            Intent(this, HomeActivity::class.java)
        )

        finish()
    }
}