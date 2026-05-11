package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.viewModels.JobViewModel

@Composable
fun ReportsScreen(viewModel: JobViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchCompletedJobsRemote()
        viewModel.fetchMechanicPerformance()
    }

    val remoteJobsResult by viewModel.remoteJobs.collectAsState()
    val performanceResult by viewModel.mechanicPerformance.collectAsState()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Reports", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Completed Jobs
        Text("Completed Jobs", style = MaterialTheme.typography.titleMedium)

        when (val result = remoteJobsResult) {
            is FirebaseResult.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FirebaseResult.Success -> {
                result.data.forEach { job ->
                    ReportItem("${job.vehicleId} - Completed by ${job.mechanicName}")
                }
            }
            is FirebaseResult.Failure -> {
                Text("Error loading jobs: ${result.exception.message}")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mechanic Performance
        Text("Mechanic Performance", style = MaterialTheme.typography.titleMedium)

        when (val result = performanceResult) {
            is FirebaseResult.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is FirebaseResult.Success -> {
                result.data.forEach { performance ->
                    ReportItem("${performance.mechanicName} - ${performance.completedJobs} jobs completed")
                }
            }
            is FirebaseResult.Failure -> {
                Text("Error loading performance: ${result.exception.message}")
            }
        }
    }
}

@Composable
fun ReportItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp)
        )
    }
}
