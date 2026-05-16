package com.example.valentine_garage.dto

data class ClientDto(
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val phone: String = "",
    val email: String = "",
    val companyName: String? = null,
    val companyPhone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
