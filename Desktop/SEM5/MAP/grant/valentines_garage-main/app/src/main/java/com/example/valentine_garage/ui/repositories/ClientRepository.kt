package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.ClientDao
import com.example.valentine_garage.database.entities.ClientEntity
import com.example.valentine_garage.dto.ClientDto
import com.example.valentine_garage.service.ManagerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepository @Inject constructor(
    private val clientDao: ClientDao,
    private val managerService: ManagerService
) {

    suspend fun insertClient(clientDto: ClientDto) {
        clientDao.insertClient(ClientEntity.fromDto(clientDto))
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

    // --- Remote ManagerService Methods ---

    suspend fun fetchAllClientsRemote() = managerService.getAllClients()
}
