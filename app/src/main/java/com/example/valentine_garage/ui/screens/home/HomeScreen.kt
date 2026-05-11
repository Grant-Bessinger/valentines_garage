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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.valentine_garage.dto.UserDto
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.WarningAmber
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.screens.components.StatCard
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.screens.components.JobTypeBreakdownSection
import com.example.valentine_garage.ui.screens.components.MechanicPerformanceSection
import com.example.valentine_garage.ui.screens.components.PerformanceSummaryCard
import com.example.valentine_garage.ui.screens.components.RecentActivitySection
import com.example.valentine_garage.ui.screens.components.RevenueTrendSection


@Composable
fun HomeScreen(navController: NavHostController, user: UserDto) {

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

            UserRole.ADMIN -> AdminHomeContent()

            UserRole.MECHANIC -> MechanicHomeContent()

            UserRole.MANAGER -> ManagerHomeContent(navController)
            else -> null
        }
    }
}

@Composable
fun AdminHomeContent() {
    Text("• Register clients")
    Text("• Register vehicles")
    Text("• Create job check-ins")
}

@Composable
fun MechanicHomeContent() {
    Text("• View assigned jobs")
    Text("• Update job status")
    Text("• Add repair notes")
}

@Composable
fun ManagerHomeContent(navController: NavHostController) {

    // 1. Performance summary banner
    PerformanceSummaryCard()

    Spacer(Modifier.height(20.dp))

    // 2. Four stat cards
    Text("Overview", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        StatCard(
            title    = "Completed Jobs",
            value    = "24",
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.CheckCircle,
            iconTint = SuccessGreen,
            subtitle = "This month"
        ) { navController.navigate("completed_jobs") }

        StatCard(
            title    = "Pending Jobs",
            value    = "6",
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
            value    = "N$ 18,450",
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.TrendingUp,
            iconTint = InfoBlue,
            subtitle = "+12% vs last month"
        ) { navController.navigate("revenue_details") }

        StatCard(
            title    = "Unpaid",
            value    = "N$ 3,200",
            modifier = Modifier.weight(1f),
            icon     = Icons.Default.Warning,
            iconTint = ErrorRed,
            subtitle = "2 invoices"
        ) { navController.navigate("unpaid_invoices") }
    }

    Spacer(Modifier.height(20.dp))

    // 3. Revenue trend bar chart
    RevenueTrendSection()

    Spacer(Modifier.height(20.dp))

    // 4. Job type breakdown
    JobTypeBreakdownSection()

    Spacer(Modifier.height(20.dp))

    // 5. Recent activity
    RecentActivitySection(navController)

    Spacer(Modifier.height(20.dp))

    // 6. Mechanic performance
    MechanicPerformanceSection()


}