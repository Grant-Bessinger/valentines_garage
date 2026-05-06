package com.example.valentine_garage.ui.screens.home.payments

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.theme.SuccessGreen

@Composable
fun PaymentsScreen() {

    val invoices = listOf(
        Payment("INV001", 1500.0, true),
        Payment("INV002", 2300.0, false),
        Payment("INV003", 800.0, true)
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Payments", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        invoices.forEach {
            PaymentItem(it)
        }
    }
}

data class Payment(
    val id: String,
    val amount: Double,
    val isPaid: Boolean
)

@Composable
fun PaymentItem(payment: Payment) {
    val statusColor = if (payment.isPaid) SuccessGreen else ErrorRed
    val statusText = if (payment.isPaid) "Paid" else "Unpaid"

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
                Text(payment.id)
                Text("N$ ${payment.amount}")
            }

            Text(
                text = statusText,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}