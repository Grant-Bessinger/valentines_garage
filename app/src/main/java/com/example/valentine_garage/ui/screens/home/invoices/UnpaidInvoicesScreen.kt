package com.example.valentine_garage.ui.screens.home.invoices

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.screens.InvoiceDetails
import com.example.valentine_garage.ui.screens.components.DetailScreen
import com.example.valentine_garage.ui.screens.components.InvoiceItem
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel

@Composable
fun UnpaidInvoicesScreen(
    navController: NavHostController,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    val unpaid by viewModel.unpaidInvoices.collectAsState()
    val total = unpaid.sumOf { it.totalCost }

    DetailScreen(title = "Unpaid Invoices", navController = navController) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = ErrorRed.copy(alpha = 0.1f)
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Unpaid", style = MaterialTheme.typography.labelMedium)
                Text(
                    "N$ %.2f".format(total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = ErrorRed
                )
            }
        }

        Spacer(Modifier.height(16.dp))

            unpaid.forEach { invoice ->
                InvoiceItem(invoice) {
                    navController.navigate(
                        InvoiceDetails.createRoute(invoice.id)
                    )
                }
            }
    }
}