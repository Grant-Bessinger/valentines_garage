package com.example.valentine_garage.ui.screens.home.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import androidx.navigation.NavHostController
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.screens.InvoiceDetails
import com.example.valentine_garage.ui.screens.components.DetailScreen
import com.example.valentine_garage.ui.screens.components.InvoiceItem
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel

@Composable
fun RevenueDetailsScreen(
    navController: NavHostController,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchRemoteInvoices()
    }

    val invoices by viewModel.allInvoices.collectAsState()

    val paidInvoices = invoices.filter { it.paid }

    val total = paidInvoices.sumOf { it.totalCost }

    DetailScreen(title = "Revenue Details", navController = navController) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    "Total Revenue",
                    style = MaterialTheme.typography.labelMedium
                )

                Text(
                    "N$ %.2f".format(total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        paidInvoices.forEach { invoice ->

            InvoiceItem(invoice) {
                navController.navigate(
                    InvoiceDetails.createRoute(invoice.id)
                )
            }
        }
    }
}
