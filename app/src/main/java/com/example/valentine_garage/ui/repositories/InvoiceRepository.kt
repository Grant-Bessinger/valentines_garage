package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.InvoiceDao
import com.example.valentine_garage.database.entities.InvoiceEntity
import com.example.valentine_garage.dto.InvoiceDto
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.service.ManagerService
import com.example.valentine_garage.service.helper.FirebaseResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class InvoiceRepository @Inject constructor(
    private val invoiceDao: InvoiceDao,
    private val managerService: ManagerService
) {

    suspend fun insertInvoice(invoiceDto: InvoiceDto) {
        invoiceDao.upsertInvoices(listOf(InvoiceEntity.fromDto(invoiceDto).copy(isSynced = false)))
    }

    suspend fun getInvoiceById(id: String): InvoiceDto? {
        return invoiceDao.getInvoiceById(id)?.toDto()
    }

    suspend fun getInvoiceByJobId(jobId: String): InvoiceDto? {
        return invoiceDao.getInvoiceByJobId(jobId)?.toDto()
    }

    fun getUnpaidInvoices(): Flow<List<InvoiceDto>> {
        return invoiceDao.getUnpaidInvoices().map { entities ->
            entities.map { it.toDto() }
        }
    }

    fun getAllInvoices(): Flow<List<InvoiceDto>> {
        return invoiceDao.getAllInvoices().map { entities ->
            entities.map { it.toDto() }
        }
    }

    suspend fun deleteInvoice(invoiceDto: InvoiceDto) {
        invoiceDao.deleteInvoice(InvoiceEntity.fromDto(invoiceDto))
    }

    // --- Remote ManagerService Methods ---

    suspend fun getUnsyncedCount(): Long = invoiceDao.getUnsyncedCount()

    suspend fun pushUnsyncedInvoices(): Int {
        val unsynced = invoiceDao.getUnsyncedInvoices()
        var count = 0
        unsynced.forEach { entity ->
            val result = managerService.saveInvoice(entity.toDto())
            if (result is FirebaseResult.Success) {
                invoiceDao.markSynced(entity.id)
                count++
            }
        }
        return count
    }

    suspend fun syncRemoteInvoices() {
        val result = managerService.getAllInvoices()
        if (result is FirebaseResult.Success) {
            val remoteInvoices = result.data
            val remoteIds = remoteInvoices.map { it.id }

            if (remoteIds.isEmpty()) {
                invoiceDao.deleteAllSyncedInvoices()
            } else {
                invoiceDao.deleteSyncedInvoicesNotInList(remoteIds)
            }

            val entities = remoteInvoices.map { InvoiceEntity.fromDto(it).copy(isSynced = true) }
            invoiceDao.upsertInvoices(entities)
        }
    }

    suspend fun generateInvoiceRemote(job: JobDto, labour: Double, parts: Double) =
        managerService.generateInvoice(job, labour, parts)

    suspend fun fetchAllInvoicesRemote() = managerService.getAllInvoices()

    suspend fun fetchUnpaidInvoicesRemote() = managerService.getUnpaidInvoices()

    suspend fun markAsPaidRemote(invoiceId: String) = managerService.markAsPaid(invoiceId)

    suspend fun getFinancialSummaryRemote() = managerService.getFinancialSummary()

    suspend fun getInvoiceByJobRemote(jobId: String) = managerService.getInvoiceByJob(jobId)
}
