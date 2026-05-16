package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.VehicleDao
import com.example.valentine_garage.database.entities.VehicleEntity
import com.example.valentine_garage.dto.VehicleDto
import com.example.valentine_garage.service.ManagerService
import com.example.valentine_garage.service.helper.FirebaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VehicleRepository @Inject constructor(
    private val vehicleDao: VehicleDao,
    private val managerService: ManagerService
) {

    suspend fun insertVehicle(vehicleDto: VehicleDto) {
        vehicleDao.insertVehicles(listOf(VehicleEntity.fromDto(vehicleDto).copy(isSynced = false)))
    }

    suspend fun getVehicleById(id: String): VehicleDto? {
        return vehicleDao.getVehicleById(id)?.toDto()
    }

    fun getVehiclesByClient(clientId: String): Flow<List<VehicleDto>> {
        return vehicleDao.getVehiclesByClient(clientId).map { entities ->
            entities.map { it.toDto() }
        }
    }

    fun getAllVehicles(): Flow<List<VehicleDto>> {
        return vehicleDao.getAllVehicles().map { entities ->
            entities.map { it.toDto() }
        }
    }

    suspend fun deleteVehicle(vehicleDto: VehicleDto) {
        vehicleDao.deleteVehicle(VehicleEntity.fromDto(vehicleDto))
    }

    // --- Remote ManagerService Methods ---

    suspend fun syncRemoteVehicles() {
        val result = managerService.getAllVehicles()
        if (result is FirebaseResult.Success) {
            val remoteVehicles = result.data
            val remoteIds = remoteVehicles.map { it.id }
            
            if (remoteIds.isEmpty()) {
                vehicleDao.deleteAllSyncedVehicles()
            } else {
                vehicleDao.deleteSyncedVehiclesNotInList(remoteIds)
            }
            
            val entities = remoteVehicles.map { VehicleEntity.fromDto(it).copy(isSynced = true) }
            vehicleDao.insertVehicles(entities)
        }
    }

    suspend fun fetchAllVehiclesRemote() = managerService.getAllVehicles()
}
