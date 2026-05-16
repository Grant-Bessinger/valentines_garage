package com.example.valentine_garage.ui.screens.home.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.ui.screens.components.JobCard
import com.example.valentine_garage.ui.viewModels.JobViewModel
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel
import com.example.valentine_garage.ui.enums.JobStatus
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun HistoryScreen(
    jobViewModel: JobViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {
    val jobs by jobViewModel.allJobs.collectAsState()
    val invoices by invoiceViewModel.allInvoices.collectAsState()
    
    val dateFormat = SimpleDateFormat("dd MMM", LocalLocale.current.platformLocale)

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Jobs", "Invoices")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Activity History",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(Modifier.height(16.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            if (selectedTabIndex == 0) {
                if (jobs.isEmpty()) {
                    EmptyHistoryPlaceholder("No jobs recorded yet.")
                } else {
                    jobs.forEach { job ->
                        JobCard(
                            vehicle = "Vehicle: ${job.vehicleId.takeLast(6).uppercase()}",
                            mechanic = job.mechanicName,
                            work = job.conditionDescription,
                            isPending = job.status != JobStatus.COMPLETED.name,
                            date = dateFormat.format(Date(job.createdAt))
                        )
                    }
                }
            } else {
                if (invoices.isEmpty()) {
                    EmptyHistoryPlaceholder("No invoices recorded yet.")
                } else {
                    invoices.forEach { invoice ->
                        HistoryInvoiceItem(
                            id = invoice.id.takeLast(6).uppercase(),
                            amount = "N$ %,.2f".format(invoice.totalCost),
                            date = dateFormat.format(Date(invoice.createdAt)),
                            isPaid = invoice.isPaid
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun HistoryInvoiceItem(id: String, amount: String, date: String, isPaid: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Invoice #$id", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Text(text = amount, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = if (isPaid) "Paid" else "Unpaid",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isPaid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun EmptyHistoryPlaceholder(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(16.dp))
            Text(text = message, color = MaterialTheme.colorScheme.outline)
        }
    }
}
