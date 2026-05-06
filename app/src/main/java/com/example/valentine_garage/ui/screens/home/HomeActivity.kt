package com.example.valentine_garage.ui.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.screens.CheckIn
import com.example.valentine_garage.ui.screens.CompletedJobs
import com.example.valentine_garage.ui.screens.Drafts
import com.example.valentine_garage.ui.screens.History
import com.example.valentine_garage.ui.screens.Home
import com.example.valentine_garage.ui.screens.Invoices
import com.example.valentine_garage.ui.screens.Payments
import com.example.valentine_garage.ui.screens.PendingJobs
import com.example.valentine_garage.ui.screens.Profile
import com.example.valentine_garage.ui.screens.Repairs
import com.example.valentine_garage.ui.screens.Reports
import com.example.valentine_garage.ui.screens.RevenueDetails
import com.example.valentine_garage.ui.screens.UnpaidInvoices
import com.example.valentine_garage.ui.screens.components.OverflowBottomSheet
import com.example.valentine_garage.ui.screens.getNavConfig
import com.example.valentine_garage.ui.screens.home.checkIn.CheckInScreen
import com.example.valentine_garage.ui.screens.home.drafts.DraftsScreen
import com.example.valentine_garage.ui.screens.home.history.HistoryScreen
import com.example.valentine_garage.ui.screens.home.invoices.InvoiceScreen
import com.example.valentine_garage.ui.screens.home.invoices.UnpaidInvoicesScreen
import com.example.valentine_garage.ui.screens.home.payments.PaymentsScreen
import com.example.valentine_garage.ui.screens.home.payments.RevenueDetailsScreen
import com.example.valentine_garage.ui.screens.home.profile.ProfileScreen
import com.example.valentine_garage.ui.screens.home.repairs.RepairsScreen
import com.example.valentine_garage.ui.screens.home.reports.CompletedJobsScreen
import com.example.valentine_garage.ui.screens.home.reports.PendingJobsScreen
import com.example.valentine_garage.ui.screens.home.reports.ReportsScreen
import com.example.valentine_garage.ui.theme.ValentineGarageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ValentineGarageApp()
        }
    }
}

@Composable
fun ValentineGarageApp() {
    ValentineGarageTheme {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        val role = UserRole.MANAGER // replace with your actual auth source
        val navConfig = getNavConfig(role)
        val bottomBarRoutes = (navConfig.primaryItems + navConfig.overflowItems)
            .map { it.route }
            .toSet()
        val showBottomBar = currentDestination?.route in bottomBarRoutes

        var showOverflowSheet by remember { mutableStateOf(false) }

        if (showOverflowSheet && navConfig.overflowItems.isNotEmpty()) {
            OverflowBottomSheet(
                items = navConfig.overflowItems,
                onItemSelected = { navController.navigateSingleTopTo(it.route) },
                onDismiss = { showOverflowSheet = false }
            )
        }

        Scaffold(
            contentWindowInsets = WindowInsets.systemBars,
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar {
                        navConfig.primaryItems.forEach { screen ->
                            NavigationBarItem(
                                icon = {
                                    screen.icon?.let {
                                        Icon(
                                            it,
                                            contentDescription = screen.route
                                        )
                                    }
                                },
                                label = { Text(screen.route.uppercase()) },
                                selected = currentDestination?.route == screen.route,
                                alwaysShowLabel = false, // replicates your expanding tab behavior
                                onClick = { navController.navigateSingleTopTo(screen.route) }
                            )
                        }

                        if (navConfig.overflowItems.isNotEmpty()) {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More"
                                    )
                                },
                                label = { Text("More") },
                                selected = false,
                                alwaysShowLabel = false,
                                onClick = { showOverflowSheet = true }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier.consumeWindowInsets(innerPadding)
            ) {

                // ── Bottom nav screens ──────────────────────────────
                composable(Home.route)     { HomeScreen(navController) }
                composable(Profile.route)  { ProfileScreen() }
                composable(History.route)  { HistoryScreen() }
                composable(CheckIn.route)  { CheckInScreen() }
                composable(Drafts.route)   { DraftsScreen() }
                composable(Repairs.route)  { RepairsScreen() }
                composable(Reports.route)  { ReportsScreen() }
                composable(Invoices.route) { InvoiceScreen() }
                composable(Payments.route) { PaymentsScreen() }

                // ── Detail screens (no bottom bar) ──────────────────
                composable(CompletedJobs.route)  { CompletedJobsScreen(navController) }
                composable(PendingJobs.route)    { PendingJobsScreen(navController) }
                composable(RevenueDetails.route) { RevenueDetailsScreen(navController) }
                composable(UnpaidInvoices.route) { UnpaidInvoicesScreen(navController) }

            }
        }
    }
}


fun NavHostController.navigateSingleTopTo(route: String) = this.navigate(route) {
    popUpTo(this@navigateSingleTopTo.graph.findStartDestination().id) {
    saveState = true
}
    launchSingleTop = true
    restoreState = true
}