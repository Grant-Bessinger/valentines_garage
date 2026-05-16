package com.example.valentine_garage.dto

data class MechanicPerformanceDto(
    val mechanicId: String = "",
    val mechanicName: String = "",
    val completedJobs: Int = 0,
    val pendingJobs: Int = 0,
    val inProgressJobs: Int = 0
)