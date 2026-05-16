package com.example.valentine_garage.ui.helper.sync

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.valentine_garage.database.dao.ClientDao
import com.example.valentine_garage.database.dao.EmployeeDao
import com.example.valentine_garage.database.dao.InvoiceDao
import com.example.valentine_garage.database.dao.JobDao
import com.example.valentine_garage.database.dao.VehicleDao
import com.google.firebase.firestore.FirebaseFirestore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.tasks.await


@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val firestore: FirebaseFirestore,
    private val clientDao: ClientDao,
    private val vehicleDao: VehicleDao,
    private val jobDao: JobDao,
    private val invoiceDao: InvoiceDao
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val CLIENTS   = "clients"
        private const val VEHICLES  = "vehicles"
        private const val JOBS      = "jobs"
        private const val INVOICES  = "invoices"

        const val TAG = "valentine_sync_worker"
    }

    override suspend fun doWork(): Result {
        return try {
            syncClients()
            syncVehicles()
            syncJobs()
            syncInvoices()
            Result.success()
        } catch (e: Exception) {
            if (runAttemptCount < 3) Result.retry() else Result.failure()
        }
    }

    // -------------------------------------------------------------------------
    // Private sync helpers
    // -------------------------------------------------------------------------

    private suspend fun syncClients() {
        val unsynced = clientDao.getUnsyncedClients()
        unsynced.forEach { entity ->
            firestore.collection(CLIENTS)
                .document(entity.id)
                .set(entity.toDto())
                .await()
            clientDao.markSynced(entity.id)
        }
    }


    private suspend fun syncVehicles() {
        val unsynced = vehicleDao.getUnsyncedVehicles()
        unsynced.forEach { entity ->
            firestore.collection(VEHICLES)
                .document(entity.id)
                .set(entity.toDto())
                .await()
            vehicleDao.markSynced(entity.id)
        }
    }

    private suspend fun syncJobs() {
        val unsynced = jobDao.getUnsyncedJobs()
        unsynced.forEach { entity ->
            firestore.collection(JOBS)
                .document(entity.id)
                .set(entity.toDto())
                .await()
            jobDao.markSynced(entity.id)
        }
    }

    private suspend fun syncInvoices() {
        val unsynced = invoiceDao.getUnsyncedInvoices()
        unsynced.forEach { entity ->
            firestore.collection(INVOICES)
                .document(entity.id)
                .set(entity.toDto())
                .await()
            invoiceDao.markSynced(entity.id)
        }
    }
}
