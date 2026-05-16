package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.EmployeeDto
import java.util.UUID

@Entity(tableName = "employees")
data class EmployeeEntity(
    @PrimaryKey
    val uid: String,
    val email: String,
    val displayName: String,
    val role: String,
    val active: Boolean,
    val isSynced: Boolean = false
) {
    fun toDto() = EmployeeDto(
        uid = uid,
        email = email,
        displayName = displayName,
        role = role,
        active = active
    )

    companion object {
        fun fromDto(dto: EmployeeDto) = EmployeeEntity(
            uid = dto.uid,
            email = dto.email,
            displayName = dto.displayName,
            role = dto.role,
            active = dto.active
        )
    }
}
