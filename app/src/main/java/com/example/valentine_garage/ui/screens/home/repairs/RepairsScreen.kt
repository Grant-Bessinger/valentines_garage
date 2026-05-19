package com.example.valentine_garage.ui.screens.home.repairs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.viewModels.AuthViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import com.example.valentine_garage.dto.UserDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairsScreen(
    user: UserDto,
    onRepairClick: (String) -> Unit = {},
    viewModel: JobViewModel = hiltViewModel()
) {

    val mechanicId = user.uid
    val allJobs by viewModel.allJobs.collectAsState()

    val dateFormat = SimpleDateFormat("dd MMM yyyy", LocalLocale.current.platformLocale)

    var selectedStatus by remember { mutableStateOf<JobStatus?>(null) }
    var showUnassignedOnly by remember { mutableStateOf(false) }
    var filterExpanded by remember { mutableStateOf(false) }

    val myJobs = remember(allJobs, mechanicId) {
        allJobs.filter { it.mechanicId == mechanicId || it.mechanicName == "Unassigned"}
    }

    val filteredJobs = myJobs.filter { job ->
        val statusMatch = selectedStatus == null || job.status == selectedStatus?.name
        val assignmentMatch = !showUnassignedOnly || job.mechanicId.isBlank() || job.mechanicId == "Unassigned"
        statusMatch && assignmentMatch
    }

    Scaffold{ padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Repairs",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Manage ongoing and past service jobs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Box {
                    IconButton(onClick = { filterExpanded = true }) {
                        Icon(Icons.Default.FilterList, contentDescription = "Filter")
                    }
                    DropdownMenu(
                        expanded = filterExpanded,
                        onDismissRequest = { filterExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Jobs") },
                            onClick = { selectedStatus = null; showUnassignedOnly = false; filterExpanded = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Unassigned Only") },
                            onClick = { showUnassignedOnly = true; filterExpanded = false }
                        )
                        JobStatus.entries.forEach { status ->
                            DropdownMenuItem(
                                text = { Text(status.name.replace("_", " ").lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() }) },
                                onClick = { selectedStatus = status; filterExpanded = false }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (filteredJobs.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when {
                            showUnassignedOnly -> "No unassigned repairs found."
                            selectedStatus != null -> "No ${selectedStatus?.name?.lowercase()} jobs."
                            else -> "No repairs found."
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(filteredJobs, key = { it.id }) { job ->
                        Box(modifier = Modifier.clickable {

                            viewModel.addJob(job.copy(
                                mechanicId = user.uid,
                                mechanicName = user.displayName
                            ))

                            onRepairClick(job.id)


                        }) {
                            JobCard(
                                vehicle = "Vehicle ID: ${job.vehicleId.takeLast(6)}",
                                mechanic = job.mechanicName,
                                work = job.conditionDescription,
                                isPending = job.status != JobStatus.COMPLETED.name,
                                date = dateFormat.format(Date(job.createdAt)),
                                priority = getPriorityForJob(job)
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getPriorityForJob(job: JobDto): String {
    return when (job.status) {
        JobStatus.PENDING.name -> "High"
        JobStatus.IN_PROGRESS.name -> "Medium"
        else -> "Low"
    }
}
