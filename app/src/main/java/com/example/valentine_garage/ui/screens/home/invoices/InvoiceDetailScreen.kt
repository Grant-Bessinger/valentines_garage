package com.example.valentine_garage.ui.screens.home.invoices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.viewModels.AuthViewModel
import com.example.valentine_garage.ui.viewModels.ClientViewModel
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import com.example.valentine_garage.ui.viewModels.VehicleViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    invoiceId: String,
    navController: NavController,
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    jobViewModel: JobViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel(),
    vehicleViewModel: VehicleViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val allInvoices by invoiceViewModel.allInvoices.collectAsState()
    val invoice = allInvoices.find { it.id == invoiceId }

    val allJobs by jobViewModel.allJobs.collectAsState()
    val job = invoice?.let { inv -> allJobs.find { it.id == inv.jobId } }

    val allClients by clientViewModel.allClients.collectAsState()
    val client = invoice?.let { inv -> allClients.find { it.id == inv.clientId } }

    val allVehicles by vehicleViewModel.allVehicles.collectAsState()
    val vehicle = job?.let { j -> allVehicles.find { it.id == j.vehicleId } }

    val currentUser by authViewModel.currentUser.collectAsState(null)
    val isAdmin = currentUser?.role == "ADMIN"

    val currentLocale = LocalLocale.current
    val dateFormat = SimpleDateFormat("dd MMM yyyy", currentLocale.platformLocale)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (invoice == null) {
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
                InvoiceStatusHeader(invoice.paid)

                Spacer(Modifier.height(24.dp))

                // --- Bill To Section ---
                InvoiceSection(
                    title = "Bill To",
                    icon = Icons.Default.Person,
                    items = listOf(
                        "Client Name" to (client?.let { "${it.name} ${it.surname}" } ?: "Unknown"),
                        "Company" to (client?.companyName ?: "N/A"),
                        "Phone" to (client?.phone ?: "N/A")
                    )
                )

                Spacer(Modifier.height(16.dp))

                // --- Vehicle Section ---
                InvoiceSection(
                    title = "Vehicle Info",
                    icon = Icons.Default.DirectionsCar,
                    items = listOf(
                        "Make / Model" to (vehicle?.let { "${it.make} ${it.model}" } ?: "Unknown"),
                        "License Plate" to (vehicle?.licensePlate ?: "N/A"),
                        "VIN" to (vehicle?.vin ?: "N/A")
                    )
                )

                Spacer(Modifier.height(16.dp))

                // --- Job & Repair Section ---
                InvoiceSection(
                    title = "Repair Summary",
                    icon = Icons.Default.Build,
                    items = listOf(
                        "Service Date" to dateFormat.format(Date(job?.createdAt ?: invoice.createdAt)),
                        "Mechanic" to (job?.mechanicName ?: "N/A")
                    )
                )

                Spacer(Modifier.height(8.dp))

                // Tasks/Fixed items
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Work Performed:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        job?.tasks?.forEach { task ->
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                                Icon(Icons.Default.Receipt, contentDescription = null, modifier = Modifier.size(14.dp), tint = SuccessGreen)
                                Spacer(Modifier.width(8.dp))
                                Text(task.description, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // --- Cost Breakdown ---
                Text("Cost Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                CostRow("Labour Cost", invoice.labourCost)
                CostRow("Parts Cost", invoice.partsCost)
                HorizontalDivider(Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Amount", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "N$ %,.2f".format(invoice.totalCost),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = InfoBlue
                    )
                }

                Spacer(Modifier.height(40.dp))

                if (!invoice.paid && isAdmin) {
                    Button(
                        onClick = {
                            invoiceViewModel.markAsPaidRemote(invoice.id)
                            // Also update locally for immediate feedback
                            invoiceViewModel.addInvoice(invoice.copy(paid = true, paidAt = System.currentTimeMillis()))
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Payments, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Confirm Payment Received")
                    }
                } else if (invoice.paid) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PAYMENT RECEIVED", fontWeight = FontWeight.ExtraBold, color = SuccessGreen)
                            invoice.paidAt?.let {
                                Text("On: ${dateFormat.format(Date(it))}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InvoiceStatusHeader(isPaid: Boolean) {
    val color = if (isPaid) SuccessGreen else Color.Gray
    val text = if (isPaid) "PAID" else "UNPAID / PENDING"

    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(Modifier.size(8.dp).background(color, RoundedCornerShape(4.dp)))
            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                fontWeight = FontWeight.ExtraBold,
                color = color,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun InvoiceSection(title: String, icon: ImageVector, items: List<Pair<String, String>>) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.primary)
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
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
fun CostRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        Text("N$ %,.2f".format(amount), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}