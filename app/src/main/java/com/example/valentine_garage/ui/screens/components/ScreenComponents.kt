package com.example.valentine_garage.ui.screens.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.theme.AccentPurple
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.AccentPurple
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.WarningAmber
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.dto.MechanicPerformanceDto
import com.example.valentine_garage.ui.enums.JobStatus
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalLocale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    title: String,
    navController: NavHostController,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            content = content
        )
    }
}

// ─── Revenue Trend ────────────────────────────────────────────────────────────

@Composable
fun RevenueTrendSection(trends: List<Pair<String, Double>> = emptyList()) {
    val months = trends.ifEmpty { listOf("Jan" to 0.0, "Feb" to 0.0, "Mar" to 0.0, "Apr" to 0.0) }
    val maxVal  = months.maxOf { it.second }.toFloat().coerceAtLeast(1f)
    val barBase = MaterialTheme.colorScheme.primary

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Revenue Trend", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Monthly revenue distribution", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                months.forEachIndexed { i, (month, value) ->
                    val isCurrent     = i == months.lastIndex
                    val heightFraction = (value.toFloat() / maxVal)
                    val barColor      = if (isCurrent) SuccessGreen else barBase.copy(alpha = 0.4f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            "N$${(value / 1_000).toInt()}k",
                            style    = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(30.dp)
                                .height((100 * heightFraction).dp)
                                .background(barColor, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(month, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
// ... rest remains similar, just adding trends param

            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(Modifier.size(10.dp).background(SuccessGreen, RoundedCornerShape(2.dp)))
                Text("Current month", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(8.dp))
                Box(Modifier.size(10.dp).background(barBase.copy(alpha = 0.4f), RoundedCornerShape(2.dp)))
                Text("Previous months", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

// ─── Job Type Breakdown ───────────────────────────────────────────────────────

@Composable
fun JobTypeBreakdownSection(breakdown: Map<String, Int> = emptyMap()) {
    val types = if (breakdown.isEmpty()) {
        listOf(
            Triple("Engine & Mechanical", 0, InfoBlue),
            Triple("Tyres & Brakes", 0, SuccessGreen),
            Triple("Electrical", 0, WarningAmber),
            Triple("Other / General", 0, AccentPurple),
        )
    } else {
        val colors = listOf(InfoBlue, SuccessGreen, WarningAmber, AccentPurple)
        breakdown.entries.toList().mapIndexed { index, entry ->
            Triple(entry.key, entry.value, colors[index % colors.size])
        }
    }
    val total = types.sumOf { it.second }.toFloat().coerceAtLeast(1f)

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Job Types", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Distribution this month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            types.forEach { (label, count, color) ->
                val fraction = count / total
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(Modifier.size(10.dp).background(color, RoundedCornerShape(2.dp)))
                    Text(label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodySmall)
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier.width(80.dp),
                        color = color,
                        trackColor = color.copy(alpha = 0.15f)
                    )
                    Text("$count", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ─── Recent Activity ──────────────────────────────────────────────────────────

@Composable
fun RecentActivitySection(navController: NavHostController, jobs: List<JobDto>) {
    val dateFormat = SimpleDateFormat("dd MMM", LocalLocale.current.platformLocale)

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            TextButton(onClick = { navController.navigate("completed_jobs") }) { Text("See all") }
        }

        jobs.take(5).forEach { job ->
            JobCard(
                vehicle = "Vehicle ID: ${job.vehicleId.takeLast(4)}",
                mechanic = job.mechanicName,
                work = job.conditionDescription,
                isPending = job.status != JobStatus.COMPLETED.name,
                date = dateFormat.format(Date(job.createdAt)),
                invoiceAmount = null // Would need invoice matching logic
            )
        }
    }
}

// ─── Mechanic Performance ─────────────────────────────────────────────────────

@Composable
 fun MechanicPerformanceSection(performances: List<MechanicPerformanceDto> = emptyList()) {

    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mechanic Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("Efficiency rating this month", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))

            performances.forEach { perf ->
                val totalJobs = perf.completedJobs + perf.pendingJobs + perf.inProgressJobs
                val efficiency = if (totalJobs > 0) perf.completedJobs.toFloat() / totalJobs else 0f
                val barColor = when {
                    efficiency >= 0.9f -> SuccessGreen
                    efficiency >= 0.8f -> WarningAmber
                    else               -> ErrorRed
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Avatar
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(perf.mechanicName.firstOrNull()?.toString() ?: "?", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Name + progress
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(perf.mechanicName, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                            Text("$totalJobs jobs", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress    = { efficiency },
                            modifier    = Modifier.fillMaxWidth(),
                            color       = barColor,
                            trackColor  = barColor.copy(alpha = 0.15f)
                        )
                    }

                    Text(
                        "${(efficiency * 100).toInt()}%",
                        style      = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color      = barColor
                    )
                }
            }
        }
    }
}
