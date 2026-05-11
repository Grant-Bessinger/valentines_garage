package com.example.valentine_garage.ui.screens.home.profile

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.ui.screens.authentication.AuthActivity
import com.example.valentine_garage.ui.viewModels.AuthViewModel


@Composable
fun ProfileScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val user by viewModel.currentUser.collectAsState()

    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {

        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text("Name: ${user?.displayName ?: "N/A"}")
        Text("Role: ${user?.role ?: "N/A"}")
        Text("Email: ${user?.email ?: "N/A"}")

        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            viewModel.logout()
            val intent = Intent(context, AuthActivity::class.java).apply {

                flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
            }

            context.startActivity(intent)
        }) {
            Text("Logout")
        }
    }
}
