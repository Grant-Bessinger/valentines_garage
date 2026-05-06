package com.example.valentine_garage.ui.screens.home.invoices

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.screens.components.DetailScreen

@Composable
fun UnpaidInvoicesScreen(navController: NavHostController) {
    val unpaid = listOf(
        Triple("INV002", "Ford Ranger", 2300.0),
        Triple("INV005", "Mazda CX-5", 900.0),
    )
    val total = unpaid.sumOf { it.third }

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

        unpaid.forEach { (invoiceId, vehicle, amount) ->
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
                        Text(invoiceId, fontWeight = FontWeight.Bold)
                        Text(vehicle, style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "N$ %.2f".format(amount),
                        color = ErrorRed,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}