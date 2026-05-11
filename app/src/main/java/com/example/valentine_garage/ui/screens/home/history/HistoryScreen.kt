package com.example.valentine_garage.ui.screens.home.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel

@Composable
fun HistoryScreen(
    jobViewModel: JobViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel()
) {
    val jobs by jobViewModel.allJobs.collectAsState()
    val invoices by invoiceViewModel.allInvoices.collectAsState()

    Column(modifier = Modifier.padding(16.dp).verticalScroll(rememberScrollState())) {

        Text("History", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        Text("Recent Local Jobs", style = MaterialTheme.typography.titleMedium)
        jobs.take(5).forEach { job ->
            HistoryItem("${job.vehicleId} - ${job.status}")
        }

        Spacer(Modifier.height(16.dp))

        Text("Recent Invoices", style = MaterialTheme.typography.titleMedium)
        invoices.take(5).forEach { invoice ->
            HistoryItem("Invoice ${invoice.id} - ${if (invoice.isPaid) "Paid" else "Unpaid"}")
        }
    }
}

@Composable
fun HistoryItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp)
        )
    }
}
