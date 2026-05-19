package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.InvoiceDto

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey
    val id: String,
    val jobId: String,
    val clientId: String,
    val labourCost: Double,
    val partsCost: Double,
    val totalCost: Double,
    val isPaid: Boolean,
    val createdAt: Long,
    val paidAt: Long?,
    val isSynced: Boolean = false
) {
    fun toDto() = InvoiceDto(
        id = id,
        jobId = jobId,
        clientId = clientId,
        labourCost = labourCost,
        partsCost = partsCost,
        totalCost = totalCost,
        paid = isPaid,
        createdAt = createdAt,
        paidAt = paidAt
    )

    companion object {
        fun fromDto(dto: InvoiceDto) = InvoiceEntity(
            id = dto.id,
            jobId = dto.jobId,
            clientId = dto.clientId,
            labourCost = dto.labourCost,
            partsCost = dto.partsCost,
            totalCost = dto.totalCost,
            isPaid = dto.paid,
            createdAt = dto.createdAt,
            paidAt = dto.paidAt
        )
    }
}
