package com.example.valentine_garage.ui.screens.components

import com.example.valentine_garage.ui.screens.BottomNavBarDestinations

data class BottomNavConfig(
    val visibleTabs: List<BottomNavBarDestinations>,
    val moreTabs: List<BottomNavBarDestinations>
)

fun buildBottomNav(roleItems: List<BottomNavBarDestinations>): BottomNavConfig {

    return if (roleItems.size <= 5) {
        BottomNavConfig(
            visibleTabs = roleItems,
            moreTabs = emptyList()
        )
    } else {
        BottomNavConfig(
            visibleTabs = roleItems.take(4),
            moreTabs = roleItems.drop(4)
        )
    }
}
