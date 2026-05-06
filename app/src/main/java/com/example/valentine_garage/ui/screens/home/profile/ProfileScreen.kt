package com.example.valentine_garage.ui.screens.home.profile

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp


@Composable
fun ProfileScreen() {
    Column(modifier = Modifier.padding(16.dp)) {

        Text("Profile", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text("Name: John Doe")
        Text("Role: Manager")
        Text("Email: john@garage.com")

        Spacer(Modifier.height(24.dp))

        Button(onClick = { /* logout */ }) {
            Text("Logout")
        }
    }
}