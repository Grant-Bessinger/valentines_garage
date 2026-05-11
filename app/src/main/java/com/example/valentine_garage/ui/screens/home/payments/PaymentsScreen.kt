package com.example.valentine_garage.ui.screens.home.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.dto.InvoiceDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel

@Composable
fun PaymentsScreen(viewModel: InvoiceViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.fetchRemoteInvoices()
    }

    val remoteInvoicesResult by viewModel.remoteInvoices.collectAsState()

    val invoices = when (val result = remoteInvoicesResult) {
        is FirebaseResult.Success -> result.data
        else -> emptyList()
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Payments", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        if (remoteInvoicesResult is FirebaseResult.Loading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            invoices.forEach {
                InvoiceItem(it)
            }
        }
    }
}

@Composable
fun InvoiceItem(invoice: InvoiceDto) {
    val statusColor = if (invoice.isPaid) SuccessGreen else ErrorRed
    val statusText = if (invoice.isPaid) "Paid" else "Unpaid"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(invoice.id)
                Text("N$ ${invoice.totalCost}")
            }

            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
