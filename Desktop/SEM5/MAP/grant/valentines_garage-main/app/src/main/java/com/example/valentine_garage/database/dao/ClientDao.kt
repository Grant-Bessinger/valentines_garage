package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.ClientEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity)

    @Query("SELECT * FROM clients WHERE id = :id")
    suspend fun getClientById(id: String): ClientEntity?

    @Query("SELECT * FROM clients")
    fun getAllClients(): Flow<List<ClientEntity>>

    @Delete
    suspend fun deleteClient(client: ClientEntity)
}
