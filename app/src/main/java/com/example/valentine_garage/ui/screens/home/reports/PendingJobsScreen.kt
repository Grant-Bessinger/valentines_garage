package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.screens.components.DetailScreen
import com.example.valentine_garage.ui.screens.components.JobCard

@Composable
fun PendingJobsScreen(navController: NavHostController) {
    val jobs = listOf(
        Triple("Nissan NP200", "Mike", "Awaiting parts"),
        Triple("Toyota Hilux", "Sarah", "Diagnostics in progress"),
        Triple("BMW 3 Series", "John", "Waiting for client approval"),
    )

    DetailScreen(title = "Pending Jobs", navController = navController) {
        Text("${jobs.size} jobs pending", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        jobs.forEach { (vehicle, mechanic, work) ->
            JobCard(vehicle = vehicle, mechanic = mechanic, work = work, isPending = true)
        }
    }
}