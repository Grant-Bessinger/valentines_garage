package com.example.valentine_garage.ui.screens

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.ui.helper.AuthState
import com.example.valentine_garage.ui.screens.authentication.AuthActivity
import com.example.valentine_garage.ui.screens.home.HomeActivity
import com.example.valentine_garage.ui.viewModels.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {


            val currentUser = FirebaseAuth
                .getInstance()
                .currentUser

            if (currentUser != null) {
                    navigateToMain()
                } else {
                    navigateToAuth()
                }
            }
    }

    private fun navigateToMain() {
        startActivity(
            Intent(this, HomeActivity::class.java)
        )

        finish()
    }


    private fun navigateToAuth() {
        startActivity(
            Intent(this, AuthActivity::class.java)
        )

        finish()
    }
}