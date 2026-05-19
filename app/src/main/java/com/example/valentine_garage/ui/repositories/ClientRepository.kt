package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.ClientDao
import com.example.valentine_garage.database.entities.ClientEntity
import com.example.valentine_garage.dto.ClientDto
import com.example.valentine_garage.service.ManagerService
import com.example.valentine_garage.service.helper.FirebaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientDao: ClientDao,
    private val managerService: ManagerService
) {

    suspend fun insertClient(clientDto: ClientDto) {
        clientDao.upsertClients(listOf(ClientEntity.fromDto(clientDto).copy(isSynced = false)))
    }

    suspend fun getClientById(id: String): ClientDto? {
        return clientDao.getClientById(id)?.toDto()
    }

    fun getAllClients(): Flow<List<ClientDto>> {
        return clientDao.getAllClients().map { entities ->
            entities.map { it.toDto() }
        }
    }

    suspend fun deleteClient(clientDto: ClientDto) {
        clientDao.deleteClient(ClientEntity.fromDto(clientDto))
    }



    suspend fun pushUnsyncedClients(): Int {
        val unsynced = clientDao.getUnsyncedClients()
        var count = 0
        unsynced.forEach { entity ->
            val result = managerService.saveClient(entity.toDto())
            if (result is FirebaseResult.Success) {
                clientDao.markSynced(entity.id)
                count++
            }
        }
        return count
    }

    suspend fun syncRemoteClients() {
        val result = managerService.getAllClients()
        if (result is com.example.valentine_garage.service.helper.FirebaseResult.Success) {
            val remoteClients = result.data
            val remoteIds = remoteClients.map { it.id }
            
            if (remoteIds.isEmpty()) {
                clientDao.deleteAllSyncedClients()
            } else {
                clientDao.deleteSyncedClientsNotInList(remoteIds)
            }
            
            val entities = remoteClients.map { ClientEntity.fromDto(it).copy(isSynced = true) }
            clientDao.upsertClients(entities)
        }
    }

    suspend fun fetchAllClientsRemote() = managerService.getAllClients()
}
