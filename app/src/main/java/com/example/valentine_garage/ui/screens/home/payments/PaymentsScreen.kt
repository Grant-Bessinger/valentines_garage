package com.example.valentine_garage.ui.screens.home.payments

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.valentine_garage.dto.InvoiceDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import androidx.navigation.NavController
import com.example.valentine_garage.ui.screens.InvoiceDetails
import com.example.valentine_garage.ui.screens.components.InvoiceItem

@Composable
fun PaymentsScreen(
    navController: NavController,
    viewModel: InvoiceViewModel = hiltViewModel()
) {

    val invoices by viewModel.allInvoices.collectAsState()
    val financialSummaryResult by viewModel.financialSummary.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRemoteInvoices()
        viewModel.fetchFinancialSummary()
    }

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Paid", "Unpaid")

    val filteredInvoices = when (selectedTabIndex) {
        1 -> invoices.filter { it.paid }
        2 -> invoices.filter { !it.paid }
        else -> invoices
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Payments & Invoices",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        // Financial Summary Mini-Cards
        if (financialSummaryResult is FirebaseResult.Success) {
            val summary = (financialSummaryResult as FirebaseResult.Success).data
            summary?.let {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    PaymentStatCard(
                        title = "Paid",
                        amount = "N$ %,.2f".format(it.paidAmount),
                        color = SuccessGreen,
                        modifier = Modifier.weight(1f)
                    )
                    PaymentStatCard(
                        title = "Unpaid",
                        amount = "N$ %,.2f".format(it.unpaidAmount),
                        color = ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))


        if (filteredInvoices.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                Text("No invoices found.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            filteredInvoices.forEach {
                InvoiceItem(it) {
                    navController.navigate(InvoiceDetails.createRoute(it.id))
                }
            }
        }
    }
}

@Composable
fun PaymentStatCard(title: String, amount: String, color: Color, modifier: Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = color)
            Text(amount, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}


