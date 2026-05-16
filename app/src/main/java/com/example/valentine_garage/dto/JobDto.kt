package com.example.valentine_garage.dto

import com.example.valentine_garage.ui.enums.JobStatus
import java.util.UUID

data class JobTaskDto(
    val id: String = "",
    val description: String = "",
    val isCompleted: Boolean = false
)

data class JobDto(
    val id: String = "",
    val clientId: String = "",
    val vehicleId: String = "",
    val mechanicId: String = "",
    val mechanicName: String = "",
    val odometerReading: Int = 0,
    val conditionDescription: String = "",
    val tasks: List<JobTaskDto> = emptyList(),
    val status: String = "",
    val notes: String? = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun toStatus(): JobStatus = JobStatus.valueOf(status)
}

