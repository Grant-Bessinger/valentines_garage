package com.example.valentine_garage.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.valentine_garage.dto.UserDto
import com.example.valentine_garage.ui.enums.UserRole

interface Screens {
    val icon: ImageVector?
    val route: String
}

// All Users
object Home : Screens {
    override val icon = Icons.Filled.Home
    override val route = "home"

}

object Profile : Screens {
    override val icon = Icons.Filled.AccountCircle
    override val route = "profile"

}


object History : Screens {
    override val icon = Icons.Filled.History
    override val route = "history"

}

// Admin
object CheckIn : Screens {
    override val icon = Icons.Filled.Checklist
    override val route = "check_in"

}

object Drafts : Screens {
    override val icon = Icons.Filled.Edit
    override val route = "drafts"

}


// Mechanic
object Repairs : Screens {
    override val icon = Icons.Filled.CarRepair
    override val route = "repairs"

}


// Valentine
object Reports : Screens {
    override val icon = Icons.Filled.Report
    override val route = "reports"

}

object Invoices : Screens {
    override val icon = Icons.Filled.Receipt
    override val route = "invoices"

}

object Payments : Screens {
    override val icon = Icons.Filled.Payments
    override val route = "payments"

}

//Detail Screens

object CompletedJobs : Screens {
    override val icon = Icons.Filled.Payments
    override val route = "completed_jobs"

}

object PendingJobs : Screens {
    override val icon = Icons.Filled.Payments
    override val route = "pending_jobs"

}

object RevenueDetails : Screens {
    override val icon = Icons.Filled.Payments
    override val route = "revenue_details"

}

object UnpaidInvoices : Screens {
    override val icon = Icons.Filled.Payments
    override val route = "unpaid_invoices"

}





data class RoleNavConfig(
    val primaryItems: List<Screens>,
    val overflowItems: List<Screens> = emptyList()
)

fun getNavConfig(user: UserDto): RoleNavConfig? {

    var userRole: UserRole? = null

    if (user.role.contains(UserRole.MECHANIC.name)){
        userRole = UserRole.MECHANIC
    } else if (user.role.contains(UserRole.ADMIN.name)){
        userRole = UserRole.ADMIN
    }else if (user.role.contains(UserRole.MANAGER.name)){
        userRole = UserRole.MANAGER
    }

    return when (userRole) {
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

        else -> null
    }
}