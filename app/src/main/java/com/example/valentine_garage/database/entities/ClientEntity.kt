package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.ClientDto

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val createdAt: Long
) {
    fun toDto() = ClientDto(
        id = id,
        name = name,
        phone = phone,
        email = email,
        createdAt = createdAt
    )

    companion object {
        fun fromDto(dto: ClientDto) = ClientEntity(
            id = dto.id,
            name = dto.name,
            phone = dto.phone,
            email = dto.email,
            createdAt = dto.createdAt
        )
    }
}
