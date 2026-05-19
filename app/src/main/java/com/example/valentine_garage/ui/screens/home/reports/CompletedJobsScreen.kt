package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.theme.AccentPurple
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.screens.components.DetailScreen
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.screens.components.SummaryTile
import com.example.valentine_garage.ui.viewModels.JobViewModel
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.screens.RepairDetails
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.tooling.preview.Preview
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel

@Composable
fun CompletedJobsScreen(
    navController: NavHostController,
    viewModel: JobViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchCompletedJobsRemote()
    }

    val remoteJobsResult by viewModel.remoteJobs.collectAsState()

    val invoices by invoiceViewModel.allInvoices.collectAsState()

    val allJobs = when (val result = remoteJobsResult) {
        is FirebaseResult.Success -> result.data
        else -> emptyList()
    }

    val filters = listOf("All", "This Week", "This Month")
    var selectedFilter by remember { mutableStateOf("All") }

    val completedJobs = allJobs.filter { it.status == JobStatus.COMPLETED.name }

    val completedJobIds = completedJobs.map { it.id }

    val relatedInvoices = invoices.filter {
        it.jobId in completedJobIds
    }

    val paidInvoices = invoices.filter {
        it.jobId in completedJobIds && it.paid
    }

    // Simplified filtering logic for DTOs
    val filtered = allJobs

    val totalRevenue = paidInvoices.sumOf { it.totalCost }

    val avgJobValue = if (relatedInvoices.isNotEmpty()) {
        totalRevenue / relatedInvoices.size
    } else {
        0.0
    }

    DetailScreen(title = "Completed Jobs", navController = navController) {
        if (remoteJobsResult is FirebaseResult.Loading) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            // ── Summary cards
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                SummaryTile(Icons.Default.CheckCircle,      "${filtered.size}",          "Jobs",         SuccessGreen, Modifier.weight(1f))
                SummaryTile(Icons.Default.MonetizationOn,   "N$ ${(totalRevenue/1000).toInt()}k", "Revenue", InfoBlue, Modifier.weight(1f))
                SummaryTile(Icons.Default.WorkHistory,      "N$ ${(avgJobValue/1000).toInt()}k",  "Avg Value", AccentPurple, Modifier.weight(1f))
            }

            Spacer(Modifier.height(16.dp))

            // ── Filter chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                filters.forEach { label ->
                    FilterChip(
                        selected = selectedFilter == label,
                        onClick  = { selectedFilter = label },
                        label    = { Text(label, style = MaterialTheme.typography.labelMedium) }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (filtered.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No jobs found for this period.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                val currentLocale = LocalLocale.current
                val dateFormat = SimpleDateFormat("dd MMM", currentLocale.platformLocale)
                filtered.forEach { job ->
                    Box(modifier = Modifier.clickable { navController.navigate(RepairDetails.createRoute(job.id)) }) {
                        JobCard(
                            vehicle = "Vehicle ID: ${job.vehicleId.takeLast(6)}",
                            mechanic = job.mechanicName,
                            work = job.conditionDescription,
                            isPending = false,
                            date = job.completedAt?.let { dateFormat.format(Date(it)) } ?: "Completed",
                            invoiceAmount = null
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
