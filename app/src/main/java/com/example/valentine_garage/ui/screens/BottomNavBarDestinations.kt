package com.example.valentine_garage.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.screens.home.HomeScreen
import com.example.valentine_garage.ui.screens.home.checkIn.CheckInScreen
import com.example.valentine_garage.ui.screens.home.drafts.DraftsScreen
import com.example.valentine_garage.ui.screens.home.history.HistoryScreen
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
    override val icon = Icons.AutoMirrored.Filled.List
    override val route = "history"
    override val screen: @Composable () -> Unit = { HistoryScreen() }
}

// Admin
object CheckIn : BottomNavBarDestinations {
    override val icon = Icons.Filled.Check
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
    override val icon = Icons.Filled.Build
    override val route = "repairs"
    override val screen: @Composable () -> Unit = { RepairsScreen() }
}


// Valentine
object Reports : BottomNavBarDestinations {
    override val icon = Icons.Filled.Info
    override val route = "reports"
    override val screen: @Composable () -> Unit = { ReportsScreen() }
}

fun getBottomNavItems(role: UserRole): List<BottomNavBarDestinations> {
    return when (role) {
        UserRole.ADMIN -> listOf(
            Home,
            CheckIn,
            Drafts,
            History,
            Profile
        )

        UserRole.MECHANIC -> listOf(
            Home,
            Repairs,
            History,
            Profile
        )

        UserRole.MANAGER -> listOf(
            Home,
            Reports,
            History,
            Profile
        )
    }
}