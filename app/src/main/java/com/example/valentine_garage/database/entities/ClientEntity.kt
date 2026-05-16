package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.ClientDto

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val surname: String,
    val phone: String,
    val email: String,
    val companyName: String?,
    val companyPhone: String?,
    val createdAt: Long,
    val isSynced: Boolean = false
) {
    fun toDto() = ClientDto(
        id = id,
        name = name,
        surname = surname,
        phone = phone,
        email = email,
        companyName = companyName,
        companyPhone = companyPhone,
        createdAt = createdAt
    )

    companion object {
        fun fromDto(dto: ClientDto) = ClientEntity(
            id = dto.id,
            name = dto.name,
            surname = dto.surname,
            phone = dto.phone,
            email = dto.email,
            companyName = dto.companyName,
            companyPhone = dto.companyPhone,
            createdAt = dto.createdAt
        )
    }
}
