package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.ClientEntity
import com.example.valentine_garage.database.entities.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoices(invoices: List<InvoiceEntity>)

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE jobId = :jobId")
    suspend fun getInvoiceByJobId(jobId: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE isPaid = 0")
    fun getUnpaidInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices WHERE isSynced = 0")
    suspend fun getUnsyncedInvoices(): List<InvoiceEntity>

    @Query("UPDATE invoices SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM invoices WHERE isSynced = 1 AND id NOT IN (:ids)")
    suspend fun deleteSyncedInvoicesNotInList(ids: List<String>)

    @Query("DELETE FROM invoices WHERE isSynced = 1")
    suspend fun deleteAllSyncedInvoices()

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)
}
