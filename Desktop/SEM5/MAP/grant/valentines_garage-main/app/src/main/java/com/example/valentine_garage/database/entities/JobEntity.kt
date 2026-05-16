package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.JobDto

@Entity(tableName = "jobs")
data class JobEntity(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val vehicleId: String,
    val mechanicId: String,
    val mechanicName: String,
    val mileage: Int,
    val conditionDescription: String,
    val status: String,
    val notes: String,
    val createdAt: Long,
    val completedAt: Long?
) {
    fun toDto() = JobDto(
        id = id,
        clientId = clientId,
        vehicleId = vehicleId,
        mechanicId = mechanicId,
        mechanicName = mechanicName,
        mileage = mileage,
        conditionDescription = conditionDescription,
        status = status,
        notes = notes,
        createdAt = createdAt,
        completedAt = completedAt
    )

    companion object {
        fun fromDto(dto: JobDto) = JobEntity(
            id = dto.id,
            clientId = dto.clientId,
            vehicleId = dto.vehicleId,
            mechanicId = dto.mechanicId,
            mechanicName = dto.mechanicName,
            mileage = dto.mileage,
            conditionDescription = dto.conditionDescription,
            status = dto.status,
            notes = dto.notes,
            createdAt = dto.createdAt,
            completedAt = dto.completedAt
        )
    }
}
