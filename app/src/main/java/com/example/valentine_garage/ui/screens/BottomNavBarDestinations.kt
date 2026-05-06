package com.example.valentine_garage.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.screens.home.HomeScreen
import com.example.valentine_garage.ui.screens.home.checkIn.CheckInScreen
import com.example.valentine_garage.ui.screens.home.drafts.DraftsScreen
import com.example.valentine_garage.ui.screens.home.history.HistoryScreen
import com.example.valentine_garage.ui.screens.home.invoices.InvoiceScreen
import com.example.valentine_garage.ui.screens.home.payments.PaymentsScreen
import com.example.valentine_garage.ui.screens.home.profile.ProfileScreen
import com.example.valentine_garage.ui.screens.home.repairs.RepairsScreen
import com.example.valentine_garage.ui.screens.home.reports.ReportsScreen

interface BottomNavBarDestinations {
    val icon: ImageVector
    val route: String
    val screen: @Composable () -> Unit
}

// All Users
object Home : BottomNavBarDestinations {
    override val icon = Icons.Filled.Home
    override val route = "home"
    override val screen: @Composable () -> Unit = { HomeScreen() }
}

object Profile : BottomNavBarDestinations {
    override val icon = Icons.Filled.AccountCircle
    override val route = "profile"
    override val screen: @Composable () -> Unit = { ProfileScreen() }
}


object History : BottomNavBarDestinations {
    override val icon = Icons.Filled.History
    override val route = "history"
    override val screen: @Composable () -> Unit = { HistoryScreen() }
}

// Admin
object CheckIn : BottomNavBarDestinations {
    override val icon = Icons.Filled.Checklist
    override val route = "check_in"
    override val screen: @Composable () -> Unit = { CheckInScreen() }
}

object Drafts : BottomNavBarDestinations {
    override val icon = Icons.Filled.Edit
    override val route = "drafts"
    override val screen: @Composable () -> Unit = { DraftsScreen() }
}


// Mechanic
object Repairs : BottomNavBarDestinations {
    override val icon = Icons.Filled.CarRepair
    override val route = "repairs"
    override val screen: @Composable () -> Unit = { RepairsScreen() }
}


// Valentine
object Reports : BottomNavBarDestinations {
    override val icon = Icons.Filled.Report
    override val route = "reports"
    override val screen: @Composable () -> Unit = { ReportsScreen() }
}

object Invoices : BottomNavBarDestinations {
    override val icon = Icons.Filled.Receipt
    override val route = "invoices"
    override val screen: @Composable () -> Unit = { InvoiceScreen() }
}

object Payments : BottomNavBarDestinations {
    override val icon = Icons.Filled.Payments
    override val route = "payments"
    override val screen: @Composable () -> Unit = { PaymentsScreen() }
}



data class RoleNavConfig(
    val primaryItems: List<BottomNavBarDestinations>,
    val overflowItems: List<BottomNavBarDestinations> = emptyList()
)

fun getNavConfig(role: UserRole): RoleNavConfig {
    return when (role) {
        UserRole.ADMIN -> RoleNavConfig(
            primaryItems = listOf(Home, CheckIn, Drafts, History, Profile)
        )
        UserRole.MECHANIC -> RoleNavConfig(
            primaryItems = listOf(Home, Repairs, History, Profile)
        )
        UserRole.MANAGER -> RoleNavConfig(
            primaryItems = listOf(Home, Reports, History, Profile),
            overflowItems = listOf(Invoices, Payments)  // moved to "More"
        )
    }
}