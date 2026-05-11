package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.valentine_garage.dto.VehicleDto

@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey
    val id: String,
    val clientId: String,
    val make: String,
    val model: String,
    val year: String,
    val licensePlate: String,
    val vin: String
) {
    fun toDto() = VehicleDto(
        id = id,
        clientId = clientId,
        make = make,
        model = model,
        year = year,
        licensePlate = licensePlate,
        vin = vin
    )

    companion object {
        fun fromDto(dto: VehicleDto) = VehicleEntity(
            id = dto.id,
            clientId = dto.clientId,
            make = dto.make,
            model = dto.model,
            year = dto.year,
            licensePlate = dto.licensePlate,
            vin = dto.vin
        )
    }
}
