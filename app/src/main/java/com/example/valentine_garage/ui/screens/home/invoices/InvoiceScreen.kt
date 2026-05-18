package com.example.valentine_garage.ui.screens.home.invoices

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.dto.InvoiceDto
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceScreen(
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    jobViewModel: JobViewModel = hiltViewModel()
) {
    val allJobs by jobViewModel.allJobs.collectAsState()
    val completedJobs = allJobs.filter { it.status == JobStatus.COMPLETED.name }

    var selectedJobId by remember { mutableStateOf("") }
    var labour by remember { mutableStateOf("") }
    var parts by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }

    val total = (labour.toDoubleOrNull() ?: 0.0) + (parts.toDoubleOrNull() ?: 0.0)

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Generate Invoice",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Create billing for completed jobs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Job Selection
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                val selectedJob = completedJobs.find { it.id == selectedJobId }
                OutlinedTextField(
                    value = selectedJob?.let { "Job: ${it.id.takeLast(6)} - ${it.mechanicName}" } ?: "Select Completed Job",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Target Job") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    if (completedJobs.isEmpty()) {
                        DropdownMenuItem(
                            text = { Text("No completed jobs available") },
                            onClick = { expanded = false }
                        )
                    } else {
                        completedJobs.forEach { job ->
                            DropdownMenuItem(
                                text = { Text("Job ${job.id.takeLast(6)} (${job.mechanicName})") },
                                onClick = {
                                    selectedJobId = job.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = labour,
                onValueChange = { labour = it },
                label = { Text("Labour Cost (N$)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = parts,
                onValueChange = { parts = it },
                label = { Text("Parts Cost (N$)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Total Amount Due", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "N$ %,.2f".format(total),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (selectedJobId.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Please select a job") }
                        return@Button
                    }
                    val selectedJob = completedJobs.find { it.id == selectedJobId } ?: return@Button

                    val invoice = InvoiceDto(
                        id = UUID.randomUUID().toString(),
                        jobId = selectedJob.id,
                        clientId = selectedJob.clientId,
                        labourCost = labour.toDoubleOrNull() ?: 0.0,
                        partsCost = parts.toDoubleOrNull() ?: 0.0,
                        totalCost = total,
                        isPaid = false,
                        createdAt = System.currentTimeMillis()
                    )
                    invoiceViewModel.addInvoice(invoice)
                    scope.launch {
                        snackbarHostState.showSnackbar("Invoice generated successfully!")
                        selectedJobId = ""
                        labour = ""
                        parts = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Confirm & Generate Invoice")
            }
        }
    }
}