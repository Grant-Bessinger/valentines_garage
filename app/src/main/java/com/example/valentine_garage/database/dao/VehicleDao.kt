package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicle(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE clientId = :clientId")
    fun getVehiclesByClient(clientId: String): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)
}
