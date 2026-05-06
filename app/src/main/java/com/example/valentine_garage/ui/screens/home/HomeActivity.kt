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
import com.example.valentine_garage.ui.screens.Home
import com.example.valentine_garage.ui.screens.components.OverflowBottomSheet
import com.example.valentine_garage.ui.screens.getNavConfig
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
        val allScreens = navConfig.primaryItems + navConfig.overflowItems
        val currentScreen = allScreens.find { it.route == currentDestination?.route }

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
                NavigationBar {
                    navConfig.primaryItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.route) },
                            label = { Text(screen.route.uppercase()) },
                            selected = currentScreen == screen,
                            alwaysShowLabel = false, // replicates your expanding tab behavior
                            onClick = { navController.navigateSingleTopTo(screen.route) }
                        )
                    }

                    if (navConfig.overflowItems.isNotEmpty()) {
                        NavigationBarItem(
                            icon = { Icon(Icons.Default.MoreVert, contentDescription = "More") },
                            label = { Text("More") },
                            selected = false,
                            alwaysShowLabel = false,
                            onClick = { showOverflowSheet = true }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = Home.route,
                modifier = Modifier.consumeWindowInsets(innerPadding)
            ) {
                allScreens.forEach { screen ->
                    composable(screen.route) { screen.screen() }
                }
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