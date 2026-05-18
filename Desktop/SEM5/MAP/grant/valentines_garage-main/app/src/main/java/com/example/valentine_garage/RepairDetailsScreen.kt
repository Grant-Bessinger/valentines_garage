package com.example.valentine_garage.ui.screens.home.repairs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalLocale
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.dto.JobTaskDto
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.WarningAmber
import com.example.valentine_garage.ui.viewModels.AuthViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RepairDetailsScreen(
    jobId: String,
    navController: NavController,
    viewModel: JobViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val allJobs by viewModel.allJobs.collectAsState()
    val job = allJobs.find { it.id == jobId }
    
    val currentUser by authViewModel.currentUser.collectAsState(null)
    val isAssignedMechanic = currentUser?.uid == job?.mechanicId
    val isAdmin = currentUser?.role == "ADMIN" || currentUser?.role == "MANAGER"

    val currentLocale = LocalLocale.current
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskDescription by remember { mutableStateOf("") }
    var mechanicNotes by remember { mutableStateOf("") }

    val allTasksDone = job?.tasks?.isNotEmpty() == true && job.tasks.all { it.isCompleted }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Repair Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (job == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                StatusHeader(job.status)
                
                Spacer(Modifier.height(24.dp))

                InfoSection(
                    title = "Vehicle & Mechanic",
                    icon = Icons.Default.Build,
                    items = listOf(
                        "Vehicle ID" to job.vehicleId,
                        "Mechanic" to job.mechanicName,
                        "Odometer" to "${job.odometerReading} km"
                    )
                )

                Spacer(Modifier.height(16.dp))

                InfoSection(
                    title = "Job Description",
                    icon = Icons.Default.History,
                    items = listOf(
                        "Reported Condition" to job.conditionDescription,
                        "Check-in Notes" to job.notes
                    )
                )

                Spacer(Modifier.height(24.dp))

                // --- Tasks Section ---
                Text("Repair Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Mark each issue as fixed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(8.dp))

                job.tasks.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = task.isCompleted,
                            onCheckedChange = { checked ->
                                val updatedTasks = job.tasks.map {
                                    if (it.id == task.id) it.copy(isCompleted = checked) else it
                                }
                                viewModel.addJob(job.copy(tasks = updatedTasks))
                            },
                            enabled = job.status != JobStatus.COMPLETED.name && (isAssignedMechanic || isAdmin)
                        )
                        Text(
                            text = task.description,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (job.status != JobStatus.COMPLETED.name && (isAssignedMechanic || isAdmin)) {
                    TextButton(
                        onClick = { showAddTaskDialog = true },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Add More Conditions / Issues")
                    }
                }

                if (job.status == JobStatus.COMPLETED.name) {
                    Spacer(Modifier.height(16.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen)
                                Spacer(Modifier.width(8.dp))
                                Text("Completion Summary", fontWeight = FontWeight.Bold, color = SuccessGreen)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "Completed on: ${SimpleDateFormat("dd MMM yyyy HH:mm", currentLocale.platformLocale).format(Date(job.completedAt ?: 0))}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                if (job.status != JobStatus.COMPLETED.name && (isAssignedMechanic || isAdmin)) {
                    Button(
                        onClick = { showCompletionDialog = true },
                        enabled = allTasksDone,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (allTasksDone) "Mark as Completed" else "Finish All Tasks First")
                    }
                }
            }
        }
    }

    if (showAddTaskDialog && job != null) {
        AlertDialog(
            onDismissRequest = { showAddTaskDialog = false },
            title = { Text("Add Vehicle Condition") },
            text = {
                OutlinedTextField(
                    value = newTaskDescription,
                    onValueChange = { newTaskDescription = it },
                    label = { Text("Issue Found") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskDescription.isNotBlank()) {
                            val newTask = JobTaskDto(description = newTaskDescription)
                            viewModel.addJob(job.copy(tasks = job.tasks + newTask))
                            newTaskDescription = ""
                            showAddTaskDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCompletionDialog && job != null) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            title = { Text("Complete Job") },
            text = {
                Column {
                    Text("Provide final details for the manager and customer.")
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = mechanicNotes,
                        onValueChange = { mechanicNotes = it },
                        label = { Text("Work Performed / Final Notes") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedJob = job.copy(
                            status = JobStatus.COMPLETED.name,
                            completedAt = System.currentTimeMillis(),
                            notes = "${job.notes}\n\n--- COMPLETION NOTES ---\n$mechanicNotes"
                        )
                        viewModel.addJob(updatedJob) // Repositories use REPLACE strategy
                        showCompletionDialog = false
                    }
                ) {
                    Text("Finish Job")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCompletionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StatusHeader(status: String) {
    val color = when(status) {
        JobStatus.COMPLETED.name -> SuccessGreen
        JobStatus.IN_PROGRESS.name -> WarningAmber
        else -> Color.Gray
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(Modifier.size(8.dp).background(color, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(12.dp))
            Text(
                text = status.replace("_", " "),
                fontWeight = FontWeight.Bold,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun InfoSection(title: String, icon: ImageVector, items: List<Pair<String, String?>>) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(8.dp))
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
        ) {
            Column(Modifier.padding(12.dp)) {
                items.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (value != null) {
                            Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}
