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
import com.example.valentine_garage.ui.enums.UserRole
import com.example.valentine_garage.ui.screens.BottomNavBarDestinations
import com.example.valentine_garage.ui.screens.Home
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
        var currentScreen: BottomNavBarDestinations by remember { mutableStateOf(Home) }
        Scaffold(
            bottomBar = {
                BottomNavBarTabRow(
                    allScreens = getBottomNavItems(UserRole.MECHANIC),
                    onTabSelected = { screen -> currentScreen = screen },
                    currentScreen = currentScreen
                )
            }
        ) { innerPadding ->
            Box(Modifier.padding(innerPadding)) {
                currentScreen.screen()
            }
        }
    }
}