package com.example.valentine_garage.ui.screens.home.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen() {
    Column(modifier = Modifier.padding(16.dp)) {

        Text("History", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        HistoryItem("Toyota Corolla - Completed")
        HistoryItem("Ford Ranger - In Progress")
        HistoryItem("VW Polo - Paid Invoice")
    }
}

@Composable
fun HistoryItem(text: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp)
        )
    }
}