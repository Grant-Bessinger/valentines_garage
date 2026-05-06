package com.example.valentine_garage.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.valentine_garage.ui.screens.BottomNavBarDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverflowBottomSheet(
    items: List<BottomNavBarDestinations>,
    onItemSelected: (BottomNavBarDestinations) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "More",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
            )
            items.forEach { screen ->
                ListItem(
                    headlineContent = {
                        Text(
                            text = screen.route.uppercase(),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    leadingContent = {
                        Icon(imageVector = screen.icon, contentDescription = screen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onItemSelected(screen)
                            onDismiss()
                        }
                )
            }
        }
    }
}