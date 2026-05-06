package com.example.valentine_garage.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.valentine_garage.ui.screens.components.StatCard
import com.example.valentine_garage.ui.enums.UserRole


@Composable
fun HomeScreen() {

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Home" }
    ) {

        Text(
            text = when (UserRole.MANAGER) {
                UserRole.ADMIN -> "Admin Home"
                UserRole.MECHANIC -> "Mechanic Home"
                UserRole.MANAGER -> "Manager Home"
            },
            style = MaterialTheme.typography.headlineMedium
        )

        when (UserRole.MANAGER) {

            UserRole.ADMIN -> AdminHomeContent()

            UserRole.MECHANIC -> MechanicHomeContent()

            UserRole.MANAGER -> ManagerHomeContent()
        }
    }
}

@Composable
fun AdminHomeContent() {
    Text("• Register clients")
    Text("• Register vehicles")
    Text("• Create job check-ins")
}

@Composable
fun MechanicHomeContent() {
    Text("• View assigned jobs")
    Text("• Update job status")
    Text("• Add repair notes")
}

@Composable
fun ManagerHomeContent() {

    Text("Financial Overview")

    Spacer(modifier = Modifier.padding(8.dp))

    StatCard("Completed Jobs", "24")
    StatCard("Revenue", "N$ 18,450")
    StatCard("Unpaid Invoices", "6")

    Spacer(modifier = Modifier.padding(12.dp))

    Text("Quick Access")

    Text("• View Reports")
    Text("• Generate Invoices")
    Text("• Track Payments")
}