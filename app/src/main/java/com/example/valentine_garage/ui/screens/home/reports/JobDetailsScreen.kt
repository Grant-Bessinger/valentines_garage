package com.example.valentine_garage.ui.screens.home.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Adjust
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.screens.home.history.EmptyHistoryPlaceholder
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.WarningAmber
import com.example.valentine_garage.ui.viewModels.JobViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailsScreen(
    jobId: String,
    navController: NavController,
    viewModel: JobViewModel = hiltViewModel()
) {

    val allJobs by viewModel.allJobs.collectAsState()

    val job = allJobs.find { it.id == jobId }

    val currentLocale = LocalLocale.current

    if (job == null) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            EmptyHistoryPlaceholder("Something went wrong retrieving this job.")
        }

    } else {

        val isCompleted = job.status == JobStatus.COMPLETED.name

        val statusColor = if (isCompleted) {
            SuccessGreen
        } else {
            WarningAmber
        }

        val statusTitle = when (job.status) {

            JobStatus.COMPLETED.name ->
                "Completed Job Summary"

            JobStatus.IN_PROGRESS.name ->
                "Job In Progress"

            JobStatus.PENDING.name ->
                "Pending Job"

            else ->
                "Job Summary"
        }

        val statusMessage = when (job.status) {

            JobStatus.COMPLETED.name ->
                "This repair job was successfully completed and archived into service history."

            JobStatus.IN_PROGRESS.name ->
                "This repair job is currently being worked on by the mechanic."

            JobStatus.PENDING.name ->
                "This repair job is waiting to be started."

            else ->
                "Job information unavailable."
        }

        Scaffold(
            topBar = {

                TopAppBar(
                    title = {
                        Text(statusTitle)
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {

                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        ) { padding ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {

                // STATUS HEADER
                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    statusColor,
                                    RoundedCornerShape(50)
                                )
                        )

                        Spacer(Modifier.width(12.dp))

                        Column {

                            Text(
                                text = "JOB ${job.status.replace("_", " ")}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = if (isCompleted) {

                                    "Completed on ${
                                        SimpleDateFormat(
                                            "dd MMM yyyy HH:mm",
                                            currentLocale.platformLocale
                                        ).format(
                                            Date(job.completedAt ?: 0)
                                        )
                                    }"

                                } else {

                                    "Job currently active"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // VEHICLE INFO
                SummarySection(
                    title = "Vehicle Information",
                    icon = Icons.Default.Build,
                    items = listOf(
                        "Vehicle ID" to job.vehicleId,
                        "Odometer" to "${job.odometerReading} km",
                        "Condition" to job.conditionDescription
                    )
                )

                Spacer(Modifier.height(20.dp))

                // MECHANIC INFO
                SummarySection(
                    title = "Mechanic Information",
                    icon = Icons.Default.Person,
                    items = listOf(
                        "Mechanic" to job.mechanicName,
                        "Status" to job.status.replace("_", " ")
                    )
                )

                Spacer(Modifier.height(20.dp))

                // TASKS SECTION
                if (job.tasks.isNotEmpty()) {

                    Text(
                        text = if (isCompleted)
                            "Completed Repairs & Checks"
                        else
                            "Assigned Repairs & Checks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {

                        Column(
                            modifier = Modifier.padding(12.dp)
                        ) {

                            job.tasks.forEach { task ->

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {

                                    Icon(
                                        if (isCompleted) Icons.Default.CheckCircle else Icons.Default.Adjust,
                                        contentDescription = null,
                                        tint = statusColor
                                    )

                                    Spacer(Modifier.width(12.dp))

                                    Text(
                                        text = task.description,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                }

                // NOTES SECTION
                Text(
                    text = if (isCompleted)
                        "Mechanic Final Notes"
                    else
                        "Admin Notes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                    )
                ) {

                    Text(
                        text = job.notes?.ifBlank {
                            "No notes added."
                        } ?: "No notes added.",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(Modifier.height(24.dp))

                // SUMMARY CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.08f)
                    )
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = statusColor
                            )

                            Spacer(Modifier.width(8.dp))

                            Text(
                                text = "Job Summary",
                                fontWeight = FontWeight.Bold,
                                color = statusColor
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Text(
                            text = statusMessage,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SummarySection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    items: List<Pair<String, String?>>
) {

    Column {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            )
        ) {

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                items.forEach { (label, value) ->

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Text(
                            text = label,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = value ?: "-",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}