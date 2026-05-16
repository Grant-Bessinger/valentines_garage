package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.UserDto

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String,
    val role: String,
    val active: Boolean
) {
    fun toDto() = UserDto(
        uid = uid,
        email = email,
        displayName = displayName,
        role = role,
        active = active
    )

    companion object {
        fun fromDto(dto: UserDto) = UserEntity(
            uid = dto.uid,
            email = dto.email,
            displayName = dto.displayName,
            role = dto.role,
            active = dto.active
        )
    }
}
