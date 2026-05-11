package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: InvoiceEntity)

    @Query("SELECT * FROM invoices WHERE id = :id")
    suspend fun getInvoiceById(id: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE jobId = :jobId")
    suspend fun getInvoiceByJobId(jobId: String): InvoiceEntity?

    @Query("SELECT * FROM invoices WHERE isPaid = 0")
    fun getUnpaidInvoices(): Flow<List<InvoiceEntity>>

    @Query("SELECT * FROM invoices")
    fun getAllInvoices(): Flow<List<InvoiceEntity>>

    @Delete
    suspend fun deleteInvoice(invoice: InvoiceEntity)
}
