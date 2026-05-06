package com.example.valentine_garage.ui.screens.home.reports

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
fun ReportsScreen() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text("Reports", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Completed Jobs
        Text("Completed Jobs", style = MaterialTheme.typography.titleMedium)

        listOf(
            "Toyota Corolla - Completed",
            "Ford Ranger - Completed",
            "VW Polo - Completed"
        ).forEach {
            ReportItem(it)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Mechanic Performance
        Text("Mechanic Performance", style = MaterialTheme.typography.titleMedium)

        listOf(
            "John - 5 jobs completed",
            "Mike - 3 jobs completed",
            "Sarah - 4 jobs completed"
        ).forEach {
            ReportItem(it)
        }
    }
}

@Composable
fun ReportItem(text: String) {
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