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

@Composable
fun PaymentsScreen(
    navController: NavController,
    viewModel: InvoiceViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchRemoteInvoices()
        viewModel.fetchFinancialSummary()
    }

    val remoteInvoicesResult by viewModel.remoteInvoices.collectAsState()
    val financialSummaryResult by viewModel.financialSummary.collectAsState()
    
    val invoices = when (val result = remoteInvoicesResult) {
        is FirebaseResult.Success -> result.data
        else -> emptyList()
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

        if (remoteInvoicesResult is FirebaseResult.Loading) {
            Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (filteredInvoices.isEmpty()) {
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

@Composable
fun InvoiceItem(invoice: InvoiceDto, onClick: () -> Unit) {
    val statusColor = if (invoice.paid) SuccessGreen else ErrorRed
    val statusText = if (invoice.paid) "PAID" else "UNPAID"
    val dateFormat = SimpleDateFormat("dd MMM yyyy", LocalLocale.current.platformLocale)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = null,
                        tint = InfoBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "INV-${invoice.id.takeLast(6).uppercase()}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.extraSmall
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Amount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("N$ %,.2f".format(invoice.totalCost), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text("Date", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(dateFormat.format(Date(invoice.createdAt)), style = MaterialTheme.typography.bodySmall)
                }
            }
            
            if (invoice.paid && invoice.paidAt != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.alpha(0.5f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Paid on ${dateFormat.format(Date(invoice.paidAt))}",
                    style = MaterialTheme.typography.labelSmall,
                    color = SuccessGreen
                )
            }
        }
    }
}
