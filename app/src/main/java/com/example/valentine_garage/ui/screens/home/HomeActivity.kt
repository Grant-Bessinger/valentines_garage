package com.example.valentine_garage.ui.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
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
import com.example.valentine_garage.ui.screens.BottomNavBarDestinations
import com.example.valentine_garage.ui.screens.CheckIn
import com.example.valentine_garage.ui.screens.Drafts
import com.example.valentine_garage.ui.screens.History
import com.example.valentine_garage.ui.screens.Home
import com.example.valentine_garage.ui.screens.Profile
import com.example.valentine_garage.ui.screens.Repairs
import com.example.valentine_garage.ui.screens.Reports
import com.example.valentine_garage.ui.screens.components.BottomNavBarTabRow
import com.example.valentine_garage.ui.screens.getBottomNavItems
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
        val currentScreen = getBottomNavItems(UserRole.MECHANIC).find { it.route == currentDestination?.route } ?: Home
        Scaffold(
            bottomBar = {
                BottomNavBarTabRow(
                    allScreens = getBottomNavItems(UserRole.MECHANIC),
                    onTabSelected = { newScreen -> navController.navigateSingleTopTo(newScreen.route) },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = Home.route) {
                    Home.screen
                }

                composable(route = Profile.route) {
                    Profile.screen
                }

                composable(route = History.route) {
                    History.screen
                }

                composable(route = Repairs.route) {
                    Repairs.screen
                }

                composable(route = Reports.route) {
                    Reports.screen
                }

                composable(route = CheckIn.route) {
                    CheckIn.screen
                }

                composable(route = Drafts.route) {
                    Drafts.screen
                }
            }
            Box(Modifier.padding(innerPadding)) {
                currentScreen.screen()
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