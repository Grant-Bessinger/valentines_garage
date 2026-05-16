package com.example.valentine_garage.ui.screens.home.drafts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.viewModels.JobViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun DraftsScreen(
    jobViewModel: JobViewModel = hiltViewModel()
) {
    val allJobs by jobViewModel.allJobs.collectAsState()
    val pendingJobs = allJobs.filter { it.status == JobStatus.PENDING.name }
    val dateFormat = SimpleDateFormat("dd MMM yyyy", LocalLocale.current.platformLocale)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Job Drafts & Pending",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Jobs awaiting assignment or start",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (pendingJobs.isEmpty()) {
            Text("No pending jobs or drafts found.")
        } else {
            pendingJobs.forEach { job ->
                JobCard(
                    vehicle = "Vehicle ID: ${job.vehicleId.takeLast(6)}",
                    mechanic = job.mechanicName.ifBlank { "Unassigned" },
                    work = job.conditionDescription,
                    isPending = true,
                    date = dateFormat.format(Date(job.createdAt))
                )
            }
        }
    }
}