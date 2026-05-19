package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.screens.components.MechanicPerformanceSection
import com.example.valentine_garage.ui.viewModels.JobViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun ReportsScreen(
    viewModel: JobViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchRemoteJobs()
        viewModel.fetchMechanicPerformance()
    }

    val remoteJobsResult by viewModel.remoteJobs.collectAsState()
    val performanceResult by viewModel.mechanicPerformance.collectAsState()
    val dateFormat = SimpleDateFormat("dd MMM yyyy", LocalLocale.current.platformLocale)

    val jobs by viewModel.allJobs.collectAsState()

    val oneMonthAgo = System.currentTimeMillis() - (30L * 24 * 60 * 60 * 1000)

    val recentJobs = jobs.filter {
        it.completedAt != null && it.completedAt >= oneMonthAgo
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Business Reports",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Insight into garage operations",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Summary Statistics
        if (performanceResult is FirebaseResult.Success && remoteJobsResult is FirebaseResult.Success) {
            val perfData = (performanceResult as FirebaseResult.Success).data
            val jobsData = (remoteJobsResult as FirebaseResult.Success).data
            
            ReportSummaryCard(
                totalCompleted = jobsData.count { it.status == "COMPLETED" },
                topMechanic = perfData.maxByOrNull { it.completedJobs }?.mechanicName ?: "N/A",
                avgEfficiency = if (perfData.isNotEmpty()) {
                    perfData.map { 
                        val total = it.completedJobs + it.pendingJobs + it.inProgressJobs
                        if (total > 0) it.completedJobs.toFloat() / total else 0f
                    }.average().toFloat()
                } else 0f
            )
            Spacer(modifier = Modifier.height(24.dp))
        }

        // Mechanic Performance Section
        Text(
            text = "Mechanic Performance",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        when (val result = performanceResult) {
            is FirebaseResult.Loading -> {
                Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FirebaseResult.Success -> {
                MechanicPerformanceSection(result.data)
            }
            is FirebaseResult.Failure -> {
                ErrorMessage("Failed to load performance: ${result.exception.message}")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Completed Jobs Section
        Text(
            text = "Recently Completed Jobs",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (recentJobs.isEmpty()) {

            Text(
                "No completed jobs found.",
                modifier = Modifier.padding(vertical = 8.dp)
            )

        } else {

            recentJobs.forEach { job ->

                JobCard(
                    vehicle = "Vehicle ID: ${job.vehicleId.takeLast(6)}",
                    mechanic = job.mechanicName,
                    work = job.conditionDescription,
                    isPending = false,
                    date = job.completedAt?.let {
                        dateFormat.format(Date(it))
                    } ?: dateFormat.format(Date(job.createdAt))
                )
            }
        }
    }
}

@Composable
fun ReportSummaryCard(
    totalCompleted: Int,
    topMechanic: String,
    avgEfficiency: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            SummaryItem(label = "Completed", value = totalCompleted.toString())
            SummaryItem(label = "Top Mechanic", value = topMechanic)
            SummaryItem(label = "Efficiency", value = "${(avgEfficiency * 100).toInt()}%")
        }
    }
}

@Composable
fun SummaryItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun ErrorMessage(message: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}
