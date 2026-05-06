package com.example.valentine_garage.ui.screens.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.valentine_garage.ui.theme.ErrorRed
import com.example.valentine_garage.ui.theme.InfoBlue
import com.example.valentine_garage.ui.theme.NeutralGrey
import com.example.valentine_garage.ui.theme.SuccessGreen
import com.example.valentine_garage.ui.theme.WarningAmber

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    iconTint: Color = Color.Unspecified,
    subtitle: String? = null,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun JobCard(
    vehicle: String,
    mechanic: String,
    work: String,
    isPending: Boolean,
    date: String? = null,
    invoiceAmount: String? = null,
    priority: String? = null
) {
    val statusColor = if (isPending) ErrorRed else SuccessGreen
    val statusText  = if (isPending) "Pending" else "Done"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(vehicle, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(2.dp))
                    Text("Mechanic: $mechanic", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        color = statusColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            color = statusColor,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    if (priority != null) {
                        Spacer(Modifier.height(4.dp))
                        val pColor = when (priority) {
                            "High"   -> ErrorRed
                            "Medium" -> WarningAmber
                            else     -> NeutralGrey
                        }
                        Surface(color = pColor.copy(alpha = 0.12f), shape = RoundedCornerShape(6.dp)) {
                            Text(
                                priority,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                color = pColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(8.dp))

            Text(work, style = MaterialTheme.typography.bodySmall)

            if (date != null || invoiceAmount != null) {
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (date != null) {
                        Text(date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (invoiceAmount != null) {
                        Text(invoiceAmount, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = InfoBlue)
                    }
                }
            }
        }
    }
}

@Composable
fun PerformanceSummaryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("This Month", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "N$ 18,450",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text("Total Revenue", style = MaterialTheme.typography.bodySmall)
                }
                Surface(
                    color = SuccessGreen.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("+12%", color = SuccessGreen, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                SummaryMetric("24",  "Completed")
                VerticalDivider(modifier = Modifier.height(36.dp))
                SummaryMetric("6",   "Pending")
                VerticalDivider(modifier = Modifier.height(36.dp))
                SummaryMetric("95%", "On Time")
            }
        }
    }
}

@Composable
private fun SummaryMetric(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun SummaryTile(
    icon: ImageVector,
    value:    String,
    label:    String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(10.dp)) {
        Column(
            modifier              = Modifier.padding(10.dp),
            horizontalAlignment   = Alignment.CenterHorizontally,
            verticalArrangement   = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}