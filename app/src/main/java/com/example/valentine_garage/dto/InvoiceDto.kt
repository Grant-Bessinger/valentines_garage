package com.example.valentine_garage.dto

data class InvoiceDto(
    val id: String = "",
    val jobId: String = "",
    val clientId: String = "",
    val labourCost: Double = 0.0,
    val partsCost: Double = 0.0,
    val totalCost: Double = 0.0,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val paidAt: Long? = null
)

