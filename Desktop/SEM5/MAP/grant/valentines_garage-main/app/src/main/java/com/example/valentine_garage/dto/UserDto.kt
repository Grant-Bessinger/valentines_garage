package com.example.valentine_garage.dto

import com.example.valentine_garage.ui.enums.UserRole

data class UserDto(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val role: String = "",
    val active: Boolean = true
) {
    fun toRole(): UserRole = UserRole.valueOf(role)
}
