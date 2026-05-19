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
    val remoteJob = allJobs.find { it.id == jobId }

    var job by remember { mutableStateOf(remoteJob) }

    var localTasks by remember { mutableStateOf<List<JobTaskDto>>(emptyList()) }

    LaunchedEffect(remoteJob) {
        remoteJob?.let {
            job = it

            if (localTasks.isEmpty()) {
                localTasks = it.tasks
            }
        }
    }

    val currentUser by authViewModel.currentUser.collectAsState(null)
    val currentLocale = LocalLocale.current
    var showCompletionDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskDescription by remember { mutableStateOf("") }
    var mechanicNotes by remember { mutableStateOf("") }

    val isLoading = allJobs.isEmpty()

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
        when {
            isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            job == null -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Job not found")
                }
            }
            else -> {
                val currentJob = job!!

                val isAssignedMechanic = currentUser?.uid == currentJob.mechanicId
                val isUnassigned = currentJob.mechanicId.isBlank() || currentJob.mechanicId == "Unassigned"
                val isMechanic = currentUser?.role == "MECHANIC"
                val isAdmin = currentUser?.role == "ADMIN" || currentUser?.role == "MANAGER"
                val allTasksDone = currentJob.tasks.isNotEmpty() && currentJob.tasks.all { it.completed }

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    StatusHeader(currentJob.status)

                    Spacer(Modifier.height(24.dp))

                    InfoSection(
                        title = "Vehicle & Mechanic",
                        icon = Icons.Default.Build,
                        items = listOf(
                            "Vehicle ID" to currentJob.vehicleId,
                            "Mechanic" to currentJob.mechanicName,
                            "Odometer" to "${currentJob.odometerReading} km"
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    InfoSection(
                        title = "Job Description",
                        icon = Icons.Default.History,
                        items = listOf(
                            "Reported Condition" to currentJob.conditionDescription,
                            "Check-in Notes" to currentJob.notes
                        )
                    )

                    Spacer(Modifier.height(24.dp))

                    Text("Repair Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Mark each issue as fixed", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))

                    localTasks.forEach { task ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = task.completed,
                                onCheckedChange = { checked ->
                                    localTasks = localTasks.map {
                                        if (it.id == task.id) {
                                            it.copy(completed = checked)
                                        } else {
                                            it
                                        }
                                    }
                                    job = currentJob.copy(tasks = localTasks)
                                },
                                enabled = currentJob.status != JobStatus.COMPLETED.name && (isAssignedMechanic || isAdmin)
                            )
                            Text(
                                text = task.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (currentJob.status != JobStatus.COMPLETED.name && (isAssignedMechanic || isAdmin)) {
                        TextButton(
                            onClick = { showAddTaskDialog = true },
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Add More Conditions / Issues")
                        }
                    }

                    if (currentJob.status == JobStatus.COMPLETED.name) {
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
                                    text = "Completed on: ${
                                        SimpleDateFormat("dd MMM yyyy HH:mm", currentLocale.platformLocale)
                                            .format(Date(currentJob.completedAt ?: 0))
                                    }",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    if (isUnassigned && isMechanic) {
                        Button(
                            onClick = {
                                currentUser?.let { user ->
                                    val updatedJob = currentJob.copy(
                                        mechanicId = user.uid,
                                        mechanicName = user.displayName,
                                        status = JobStatus.IN_PROGRESS.name
                                    )
                                    job = updatedJob
                                    viewModel.addJob(updatedJob)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Assign to Me & Start Work")
                        }
                        Spacer(Modifier.height(12.dp))
                    }

                    if (currentJob.status != JobStatus.COMPLETED.name && (isAssignedMechanic || isAdmin)) {
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

                if (showAddTaskDialog) {
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
                                        val newTask = JobTaskDto(
                                            id = UUID.randomUUID().toString(),
                                            description = newTaskDescription
                                        )
                                        localTasks = localTasks + newTask

                                        job = job!!.copy(
                                            tasks = localTasks
                                        )

                                        newTaskDescription = ""
                                        showAddTaskDialog = false
                                    }
                                }
                            ) { Text("Add") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showAddTaskDialog = false }) { Text("Cancel") }
                        }
                    )
                }

                if (showCompletionDialog) {
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

                                    val updatedJob = job!!.copy(
                                        status = JobStatus.COMPLETED.name,
                                        tasks = localTasks,
                                        completedAt = System.currentTimeMillis(),
                                        notes = "${job!!.notes}\n\n--- COMPLETION NOTES ---\n$mechanicNotes"
                                    )

                                    job = updatedJob

                                    viewModel.addJob(updatedJob)

                                    showCompletionDialog = false
                                }
                            ) {
                                Text("Finish Job")
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showCompletionDialog = false }) { Text("Cancel") }
                        }
                    )
                }
            }
        }
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
