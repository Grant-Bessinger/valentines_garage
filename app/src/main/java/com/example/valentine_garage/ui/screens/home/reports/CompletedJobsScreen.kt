package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.WorkHistory
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.theme.AccentPurple
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.screens.components.DetailScreen
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.screens.components.SummaryTile
import com.example.valentine_garage.ui.testModels.CompletedJob

@Composable
fun CompletedJobsScreen(navController: NavHostController) {

    val allJobs = listOf(
        CompletedJob(
            "Toyota Corolla",
            "John",
            "Oil change + brake pads",
            "30 Apr 2025",
            "INV001",
            3200.0,
            "Week"
        ),
        CompletedJob("VW Polo",        "Sarah", "Tyre replacement",         "1 May 2025",  "INV003", 2450.0, "Week"),
        CompletedJob("Honda Fit",      "John",  "Transmission service",     "28 Apr 2025", "INV004", 7000.0, "Month"),
        CompletedJob("Ford Ranger",    "Mike",  "Engine tune-up",           "20 Apr 2025", "INV002", 5800.0, "Month"),
        CompletedJob("Mazda CX-5",     "Sarah", "Suspension overhaul",      "5 Apr 2025",  "INV006", 4100.0, "All"),
        CompletedJob("BMW 3 Series",   "John",  "Full service + filters",   "10 Mar 2025", "INV007", 6200.0, "All"),
    )

    val filters = listOf("All", "This Week", "This Month")
    var selectedFilter by remember { mutableStateOf("All") }

    val filtered = when (selectedFilter) {
        "This Week"  -> allJobs.filter { it.filter == "Week" }
        "This Month" -> allJobs.filter { it.filter in listOf("Week", "Month") }
        else         -> allJobs
    }

    val totalRevenue = filtered.sumOf { it.amount }
    val avgJobValue  = if (filtered.isNotEmpty()) totalRevenue / filtered.size else 0.0

    DetailScreen(title = "Completed Jobs", navController = navController) {

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
            filtered.forEach { job ->
                JobCard(
                    vehicle = job.vehicle,
                    mechanic = job.mechanic,
                    work = job.work,
                    isPending = false,
                    date = job.date,
                    invoiceAmount = "N$ %.2f".format(job.amount)
                )
            }

            Spacer(Modifier.height(12.dp))

            // ── Total row
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors   = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape    = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total Revenue (${filtered.size} jobs)", fontWeight = FontWeight.Bold)
                    Text("N$ %.2f".format(totalRevenue), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}


