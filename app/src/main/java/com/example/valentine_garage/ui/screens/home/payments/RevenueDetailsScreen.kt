package com.example.valentine_garage.ui.screens.home.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.valentine_garage.ui.screens.components.DetailScreen

@Composable
fun RevenueDetailsScreen(navController: NavHostController) {
    val entries = listOf(
        Pair("Toyota Corolla - INV001", 3200.0),
        Pair("Ford Ranger - INV002", 5800.0),
        Pair("VW Polo - INV003", 2450.0),
        Pair("Honda Fit - INV004", 7000.0),
    )
    val total = entries.sumOf { it.second }

    DetailScreen(title = "Revenue Details", navController = navController) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Total Revenue", style = MaterialTheme.typography.labelMedium)
                Text(
                    "N$ %.2f".format(total),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        entries.forEach { (label, amount) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(label, style = MaterialTheme.typography.bodyMedium)
                Text(
                    "N$ %.2f".format(amount),
                    fontWeight = FontWeight.SemiBold
                )
            }
            HorizontalDivider()
        }
    }
}