package com.example.valentine_garage.dto

data class ClientDto(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
