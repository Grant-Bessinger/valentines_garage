package com.example.valentine_garage.dto

import com.example.valentine_garage.ui.enums.JobStatus

data class JobDto(
    val id: String = "",
    val clientId: String = "",
    val vehicleId: String = "",
    val mechanicId: String = "",
    val mechanicName: String = "",
    val mileage: Int = 0,
    val conditionDescription: String = "",
    val status: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
) {
    fun toStatus(): JobStatus = JobStatus.valueOf(status)
}

