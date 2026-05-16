package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.ClientEntity
import com.example.valentine_garage.database.entities.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVehicles(vehicles: List<VehicleEntity>)

    @Query("SELECT * FROM vehicles WHERE id = :id")
    suspend fun getVehicleById(id: String): VehicleEntity?

    @Query("SELECT * FROM vehicles WHERE clientId = :clientId")
    fun getVehiclesByClient(clientId: String): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles")
    fun getAllVehicles(): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE isSynced = 0")
    suspend fun getUnsyncedVehicles(): List<VehicleEntity>

    @Query("UPDATE vehicles SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM vehicles WHERE isSynced = 1 AND id NOT IN (:ids)")
    suspend fun deleteSyncedVehiclesNotInList(ids: List<String>)

    @Query("DELETE FROM vehicles WHERE isSynced = 1")
    suspend fun deleteAllSyncedVehicles()

    @Delete
    suspend fun deleteVehicle(vehicle: VehicleEntity)
}
