package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.navigation.NavHostController
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.screens.components.DetailScreen
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.viewModels.JobViewModel

@Composable
fun PendingJobsScreen(
    navController: NavHostController,
    viewModel: JobViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchRemoteJobs()
    }

    val remoteJobsResult by viewModel.remoteJobs.collectAsState()

    val pendingJobs = when (val result = remoteJobsResult) {
        is FirebaseResult.Success -> result.data.filter { it.status == JobStatus.PENDING.name }
        else -> emptyList()
    }

    DetailScreen(title = "Pending Jobs", navController = navController) {
        if (remoteJobsResult is FirebaseResult.Loading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Text("${pendingJobs.size} jobs pending", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(12.dp))
            pendingJobs.forEach { job ->
                JobCard(
                    vehicle = "Vehicle: ${job.vehicleId}",
                    mechanic = job.mechanicName,
                    work = job.conditionDescription,
                    isPending = true
                )
            }
        }
    }
}
