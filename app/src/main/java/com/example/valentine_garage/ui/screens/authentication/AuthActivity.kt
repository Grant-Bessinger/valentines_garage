package com.example.valentine_garage.ui.screens.authentication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.valentine_garage.ui.screens.home.MainActivity
import com.example.valentine_garage.ui.theme.ValentineGarageTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ValentineGarageTheme {

                LoginScreen(
                    { navigateToMain() }
                )
            }
        }
    }

    private fun navigateToMain() {
        startActivity(
            Intent(this, MainActivity::class.java)
        )

        finish()
    }
}