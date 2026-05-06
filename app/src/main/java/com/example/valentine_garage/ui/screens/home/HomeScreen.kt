package com.example.valentine_garage.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
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
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.screens.components.StatCard
import com.example.valentine_garage.ui.enums.UserRole


@Composable
fun HomeScreen(navController: NavHostController) {

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

            UserRole.MANAGER -> ManagerHomeContent(navController)
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
fun ManagerHomeContent(navController: NavHostController) {

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        StatCard(
            "Completed Jobs",
            "24",
            Modifier.weight(1f)
        ) {
            navController.navigate("completed_jobs")
        }

        StatCard(
            "Pending Jobs",
            "6",
            Modifier.weight(1f)
        ) {
            navController.navigate("pending_jobs")
        }
    }

    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {

        StatCard(
            "Revenue",
            "N$ 18,450",
            Modifier.weight(1f)
        ) {
            navController.navigate("revenue_details")
        }

        StatCard(
            "Unpaid",
            "N$ 3,200",
            Modifier.weight(1f)
        ) {
            navController.navigate("unpaid_invoices")
        }
    }
}