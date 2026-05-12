package com.example.valentine_garage.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.valentine_garage.dto.UserDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.WarningAmber
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.screens.components.StatCard
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.enums.JobStatus
import com.example.valentine_garage.ui.screens.components.JobTypeBreakdownSection
import com.example.valentine_garage.ui.screens.components.MechanicPerformanceSection
import com.example.valentine_garage.ui.screens.components.PerformanceSummaryCard
import com.example.valentine_garage.ui.screens.components.RecentActivitySection
import com.example.valentine_garage.ui.screens.components.RevenueTrendSection
import com.example.valentine_garage.ui.viewModels.ClientViewModel
import com.example.valentine_garage.ui.viewModels.InvoiceViewModel
import com.example.valentine_garage.ui.viewModels.JobViewModel
import com.example.valentine_garage.ui.viewModels.VehicleViewModel


@Composable
fun HomeScreen(
    navController: NavHostController,
    user: UserDto,
    jobViewModel: JobViewModel = hiltViewModel(),
    invoiceViewModel: InvoiceViewModel = hiltViewModel(),
    clientViewModel: ClientViewModel = hiltViewModel(),
    vehicleViewModel: VehicleViewModel = hiltViewModel()
) {

    var userRole: UserRole? = null

    if (user.role.contains(UserRole.MECHANIC.name)){
        userRole = UserRole.MECHANIC
    } else if (user.role.contains(UserRole.ADMIN.name)){
        userRole = UserRole.ADMIN
    }else if (user.role.contains(UserRole.MANAGER.name)){
        userRole = UserRole.MANAGER
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .semantics { contentDescription = "Home" }
    ) {

        when (userRole) {
            UserRole.ADMIN -> "Admin Home"
            UserRole.MECHANIC -> "Mechanic Home"
            UserRole.MANAGER -> "Manager Home"
            else -> null
        }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineMedium
            )
        }

        when (userRole) {

            UserRole.ADMIN -> AdminHomeContent(navController, clientViewModel, vehicleViewModel, jobViewModel)

            UserRole.MECHANIC -> MechanicHomeContent(navController, jobViewModel, user.uid)

            UserRole.MANAGER -> ManagerHomeContent(navController, jobViewModel, invoiceViewModel)
            else -> null
        }
    }
}

@Composable
fun AdminHomeContent(
    navController: NavHostController,
    clientViewModel: ClientViewModel,
    vehicleViewModel: VehicleViewModel,
    jobViewModel: JobViewModel
) {
    val allClients by clientViewModel.allClients.collectAsState()
    val allVehicles by vehicleViewModel.allVehicles.collectAsState()
    val allJobs by jobViewModel.allJobs.collectAsState()

    LaunchedEffect(Unit) {
        clientViewModel.fetchRemoteClients()
        vehicleViewModel.fetchRemoteVehicles()
        jobViewModel.fetchRemoteJobs()
    }

    Text("Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            title    = "Total Clients",
            value    = allClients.size.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.People,
            iconTint = InfoBlue
        ) { /* Navigate to client list if implemented */ }

        StatCard(
            title    = "Total Vehicles",
            value    = allVehicles.size.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.DirectionsCar,
            iconTint = SuccessGreen
        ) { /* Navigate to vehicle list if implemented */ }
    }

    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        val activeJobsCount = allJobs.count { it.status == JobStatus.PENDING.name || it.status == JobStatus.IN_PROGRESS.name }
        StatCard(
            title    = "Active Jobs",
            value    = activeJobsCount.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.Build,
            iconTint = WarningAmber
        ) { navController.navigate("pending_jobs") }

        Spacer(Modifier.weight(1f))
    }

    Spacer(Modifier.height(20.dp))
    Text("Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text("• Register clients", style = MaterialTheme.typography.bodyMedium)
    Text("• Register vehicles", style = MaterialTheme.typography.bodyMedium)
    Text("• Create job check-ins", style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun MechanicHomeContent(
    navController: NavHostController,
    jobViewModel: JobViewModel,
    mechanicId: String
) {
    val myJobs by jobViewModel.getJobsByMechanic(mechanicId).collectAsState()

    LaunchedEffect(mechanicId) {
        jobViewModel.fetchRemoteJobs()
    }

    val pendingJobs = myJobs.count { it.status == JobStatus.PENDING.name }
    val inProgressJobs = myJobs.count { it.status == JobStatus.IN_PROGRESS.name }
    val completedJobs = myJobs.count { it.status == JobStatus.COMPLETED.name }

    Text("My Tasks", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            title    = "Assigned",
            value    = pendingJobs.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.AutoMirrored.Filled.Assignment,
            iconTint = InfoBlue
        ) { navController.navigate("repairs") }

        StatCard(
            title    = "In Progress",
            value    = inProgressJobs.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.Build,
            iconTint = WarningAmber
        ) { navController.navigate("repairs") }
    }

    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            title    = "Completed",
            value    = completedJobs.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.CheckCircle,
            iconTint = SuccessGreen
        ) { navController.navigate("history") }

        Spacer(Modifier.weight(1f))
    }

    Spacer(Modifier.height(20.dp))
    Text("Work Logs", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text("• View assigned jobs", style = MaterialTheme.typography.bodyMedium)
    Text("• Update job status", style = MaterialTheme.typography.bodyMedium)
    Text("• Add repair notes", style = MaterialTheme.typography.bodyMedium)
}

@Composable
fun ManagerHomeContent(
    navController: NavHostController,
    jobViewModel: JobViewModel,
    invoiceViewModel: InvoiceViewModel
) {

    val allJobs by jobViewModel.allJobs.collectAsState()
    val mechanicPerformanceState by jobViewModel.mechanicPerformance.collectAsState()
    val financialSummaryState by invoiceViewModel.financialSummary.collectAsState()
    val unpaidInvoices by invoiceViewModel.unpaidInvoices.collectAsState()

    LaunchedEffect(Unit) {
        jobViewModel.fetchRemoteJobs()
        jobViewModel.fetchMechanicPerformance()
        invoiceViewModel.fetchFinancialSummary()
    }

    val completedJobsCount = allJobs.count { it.status == JobStatus.COMPLETED.name }
    val pendingJobsCount = allJobs.count { it.status == JobStatus.PENDING.name || it.status == JobStatus.IN_PROGRESS.name }
    
    val onTimeRate = if (allJobs.isNotEmpty()) {
        "${(completedJobsCount.toFloat() / allJobs.size * 100).toInt()}%"
    } else "0%"

    val financialSummary = when (val state = financialSummaryState) {
        is FirebaseResult.Success -> state.data
        else -> null
    }

    val performances = when (val state = mechanicPerformanceState) {
        is FirebaseResult.Success -> state.data
        else -> emptyList()
    }
    val revenue = financialSummary?.totalRevenue ?: 0.0
    val unpaidAmount = financialSummary?.unpaidAmount ?: 0.0
    val unpaidCount = financialSummary?.unpaidInvoices ?: unpaidInvoices.size

    val jobTypeBreakdown = allJobs.groupBy { job ->
        when {
            job.conditionDescription.contains("Engine", ignoreCase = true) || 
            job.conditionDescription.contains("Mechanical", ignoreCase = true) -> "Engine & Mechanical"
            job.conditionDescription.contains("Tyre", ignoreCase = true) || 
            job.conditionDescription.contains("Brake", ignoreCase = true) -> "Tyres & Brakes"
            job.conditionDescription.contains("Electrical", ignoreCase = true) -> "Electrical"
            else -> "Other / General"
        }
    }.mapValues { it.value.size }

    val revenueTrend = financialSummary?.let {
        listOf(
            "Target" to (it.totalRevenue * 1.2),
            "Actual" to it.totalRevenue
        )
    } ?: emptyList()

    // 1. Performance summary banner
    PerformanceSummaryCard(
        revenue = "N$ %,.2f".format(revenue),
        completedCount = completedJobsCount.toString(),
        pendingCount = pendingJobsCount.toString(),
        onTimeRate = onTimeRate
    )

    Spacer(Modifier.height(20.dp))

    // 2. Four stat cards
    Text("Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            title    = "Completed Jobs",
            value    = completedJobsCount.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.CheckCircle,
            iconTint = SuccessGreen,
            subtitle = "Total"
        ) { navController.navigate("completed_jobs") }

        StatCard(
            title    = "Pending Jobs",
            value    = pendingJobsCount.toString(),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.HourglassEmpty,
            iconTint = WarningAmber,
            subtitle = "In progress"
        ) { navController.navigate("pending_jobs") }
    }

    Spacer(Modifier.height(12.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            title    = "Revenue",
            value    = "N$ %,.2f".format(revenue),
            modifier = Modifier.weight(1f),
            icon     = Icons.AutoMirrored.Filled.TrendingUp,
            iconTint = InfoBlue,
            subtitle = "Total Revenue"
        ) { navController.navigate("revenue_details") }

        StatCard(
            title    = "Unpaid",
            value    = "N$ %,.2f".format(unpaidAmount),
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.Warning,
            iconTint = ErrorRed,
            subtitle = "$unpaidCount invoices"
        ) { navController.navigate("unpaid_invoices") }
    }

    Spacer(Modifier.height(20.dp))

    // 3. Revenue trend bar chart
    RevenueTrendSection(revenueTrend)

    Spacer(Modifier.height(20.dp))

    // 4. Job type breakdown
    JobTypeBreakdownSection(jobTypeBreakdown)

    Spacer(Modifier.height(20.dp))

    // 5. Recent activity
    RecentActivitySection(navController, allJobs)

    Spacer(Modifier.height(20.dp))

    // 6. Mechanic performance
    MechanicPerformanceSection(performances)


}
