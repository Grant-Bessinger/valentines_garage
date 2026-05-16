package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClients(clients: List<ClientEntity>)

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: String): ClientEntity?

    @Query("SELECT * FROM clients")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE isSynced = 0")
    suspend fun getUnsyncedClients(): List<ClientEntity>

    @Query("UPDATE clients SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM clients WHERE isSynced = 1 AND id NOT IN (:ids)")
    suspend fun deleteSyncedClientsNotInList(ids: List<String>)

    @Query("DELETE FROM clients WHERE isSynced = 1")
    suspend fun deleteAllSyncedClients()

    @Delete
    suspend fun deleteClient(client: ClientEntity)
}
