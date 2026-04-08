package com.example.valentine_garage.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigInteger
import kotlin.random.Random

@Entity
data class TruckEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleRegNum: String,
    val vehicleMake: String,
    val ownerName: String,
    val contact: String,
    val odometerReading: BigInteger,
    val vehicleCondition: String,
    val conditionNotes: String,
    val serviceRequired: List<String>

)
