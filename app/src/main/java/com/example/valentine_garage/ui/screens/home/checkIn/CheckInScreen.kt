package com.example.valentine_garage.ui.screens.home.checkIn

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.dto.ClientDto
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.dto.JobTaskDto
import com.example.valentine_garage.dto.VehicleDto
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.viewModels.ClientViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import com.example.valentine_garage.ui.viewModels.VehicleViewModel
import com.example.valentine_garage.ui.viewModels.AuthViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInScreen(
    jobViewModel: JobViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel(),
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState(null)
    val isMechanic = currentUser?.role == "MECHANIC"

    if (isMechanic) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Mechanics are not authorized to create new jobs.")
        }
        return
    }

    val allEmployees by authViewModel.allEmployees.collectAsState()
    val mechanics = allEmployees.filter { it.role.contains("MECHANIC") }

    // Client Input
    var clientName by remember { mutableStateOf("") }
    var clientSurname by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var clientEmail by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var companyPhone by remember { mutableStateOf("") }

    // Vehicle Input
    var vehicleMake by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var vin by remember { mutableStateOf("") }
    var odometerReading by remember { mutableStateOf("") }

    // Job Input
    var selectedMechanicId by remember { mutableStateOf("") }
    val conditions = remember { mutableStateListOf("") }
    var notes by remember { mutableStateOf("") }

    var mechanicExpanded by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        authViewModel.syncMechanics()
    }

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
                text = "Vehicle Check-In",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Client Section ---
            Text("Client Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Name") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = clientSurname,
                    onValueChange = { clientSurname = it },
                    label = { Text("Surname") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = clientPhone,
                onValueChange = { clientPhone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = clientEmail,
                onValueChange = { clientEmail = it },
                label = { Text("Email (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text("Company Details (Optional)", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(
                value = companyName,
                onValueChange = { companyName = it },
                label = { Text("Company Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = companyPhone,
                onValueChange = { companyPhone = it },
                label = { Text("Company Phone") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Vehicle Section ---
            Text("Vehicle Information", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = vehicleMake,
                    onValueChange = { vehicleMake = it },
                    label = { Text("Make") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = vehicleModel,
                    onValueChange = { vehicleModel = it },
                    label = { Text("Model") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licensePlate,
                onValueChange = { licensePlate = it },
                label = { Text("License Plate") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = vin,
                onValueChange = { vin = it },
                label = { Text("VIN (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = odometerReading,
                onValueChange = { odometerReading = it },
                label = { Text("Odometer Reading") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // --- Job/Assignment Section ---
            Text("Job Assignment", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = mechanicExpanded,
                onExpandedChange = { mechanicExpanded = !mechanicExpanded }
            ) {
                val selectedMechanic = mechanics.find { it.uid == selectedMechanicId }
                OutlinedTextField(
                    value = selectedMechanic?.displayName ?: "Assign Mechanic",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Mechanic") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = mechanicExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = mechanicExpanded,
                    onDismissRequest = { mechanicExpanded = false }
                ) {
                    mechanics.forEach { mechanic ->
                        DropdownMenuItem(
                            text = { Text(mechanic.displayName) },
                            onClick = {
                                selectedMechanicId = mechanic.uid
                                mechanicExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Reported Conditions / Issues", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            conditions.forEachIndexed { index, condition ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = condition,
                        onValueChange = { conditions[index] = it },
                        label = { Text("Condition ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    if (conditions.size > 1) {
                        IconButton(onClick = { conditions.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove Condition", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }

            TextButton(
                onClick = { conditions.add("") },
                modifier = Modifier.align(Alignment.Start)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add Another Condition")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Internal Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (clientName.isBlank() || vehicleMake.isBlank() || licensePlate.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Please fill in basic client and vehicle info") }
                        return@Button
                    }

                    scope.launch {
                        val clientId = UUID.randomUUID().toString()
                        val vehicleId = UUID.randomUUID().toString()
                        val jobId = UUID.randomUUID().toString()

                        val client = ClientDto(
                            id = clientId,
                            name = clientName,
                            surname = clientSurname,
                            phone = clientPhone,
                            email = clientEmail,
                            companyName = companyName.ifBlank { null },
                            companyPhone = companyPhone.ifBlank { null }
                        )

                        val vehicle = VehicleDto(
                            id = vehicleId,
                            clientId = clientId,
                            make = vehicleMake,
                            model = vehicleModel,
                            licensePlate = licensePlate,
                            vin = vin
                        )

                        val job = JobDto(
                            id = jobId,
                            clientId = clientId,
                            vehicleId = vehicleId,
                            mechanicId = selectedMechanicId,
                            mechanicName = mechanics.find { it.uid == selectedMechanicId }?.displayName ?: "Unassigned",
                            odometerReading = odometerReading.toIntOrNull() ?: 0,
                            conditionDescription = conditions.firstOrNull { it.isNotBlank() } ?: "No description",
                            tasks = conditions.filter { it.isNotBlank() }.map { JobTaskDto(description = it) },
                            status = JobStatus.PENDING.name,
                            notes = notes
                        )

                        // Save locally
                        clientViewModel.addClient(client)
                        vehicleViewModel.addVehicle(vehicle)
                        jobViewModel.addJob(job)

                        snackbarHostState.showSnackbar("Vehicle checked in successfully!")

                        // Reset all fields
                        clientName = ""; clientSurname = ""; clientPhone = ""; clientEmail = ""
                        companyName = ""; companyPhone = ""
                        vehicleMake = ""; vehicleModel = ""; licensePlate = ""; vin = ""; odometerReading = ""
                        selectedMechanicId = ""; notes = ""
                        conditions.clear()
                        conditions.add("")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Create Job Check-In")
            }
        }
    }
}