package com.example.valentine_garage.dto


data class FinancialSummaryDto(
    val totalRevenue: Double = 0.0,
    val paidAmount: Double = 0.0,
    val unpaidAmount: Double = 0.0,
    val totalInvoices: Int = 0,
    val paidInvoices: Int = 0,
    val unpaidInvoices: Int = 0
)
