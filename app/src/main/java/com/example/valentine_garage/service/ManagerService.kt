package com.example.valentine_garage.service

import com.example.valentine_garage.database.entities.JobEntity
import com.example.valentine_garage.dto.ClientDto
import com.example.valentine_garage.dto.FinancialSummaryDto
import com.example.valentine_garage.dto.InvoiceDto
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.dto.MechanicPerformanceDto
import com.example.valentine_garage.dto.VehicleDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.enums.JobStatus
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class ManagerService @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val JOBS = "jobs"
        private const val INVOICES = "invoices"
        private const val USERS = "users"
        private const val CLIENTS = "clients"
        private const val VEHICLES = "vehicles"
    }

    suspend fun getAllClients(): FirebaseResult<List<ClientDto>> {
        return try {
            val snapshot = firestore.collection(CLIENTS).get().await()
            FirebaseResult.Success(snapshot.toObjects(ClientDto::class.java))
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getAllVehicles(): FirebaseResult<List<VehicleDto>> {
        return try {
            val snapshot = firestore.collection(VEHICLES).get().await()
            FirebaseResult.Success(snapshot.toObjects(VehicleDto::class.java))
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun createJob(
        job: JobDto
    ): FirebaseResult<JobDto> {
        return try {

            val existing = firestore.collection(INVOICES)
                .whereEqualTo("jobId", job.id)
                .get()
                .await()

            if (!existing.isEmpty) {
                val existingJob = existing.toObjects(JobDto::class.java).first()
                return FirebaseResult.Success(existingJob)
            }

            firestore.collection(INVOICES).document(job.id).set(job).await()
            FirebaseResult.Success(job)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getCompletedJobs(): FirebaseResult<List<JobDto>> {
        return try {
            val snapshot = firestore.collection(JOBS)
                .whereEqualTo("status", JobStatus.COMPLETED.name)
                .get()
                .await()
            val jobs = snapshot.toObjects(JobDto::class.java)
                .sortedByDescending { it.completedAt }
            FirebaseResult.Success(jobs)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getAllJobs(): FirebaseResult<List<JobDto>> {
        return try {
            val snapshot = firestore.collection(JOBS).get().await()
            FirebaseResult.Success(snapshot.toObjects(JobDto::class.java))
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getMechanicPerformance(): FirebaseResult<List<MechanicPerformanceDto>> {
        return try {
            val jobsSnapshot = firestore.collection(JOBS).get().await()
            val jobs = jobsSnapshot.toObjects(JobDto::class.java)

            // Group jobs by mechanic
            val grouped = jobs.groupBy { it.mechanicId }

            val performanceList = grouped.map { (mechanicId, mechanicJobs) ->
                MechanicPerformanceDto(
                    mechanicId = mechanicId,
                    mechanicName = mechanicJobs.firstOrNull()?.mechanicName ?: "Unknown",
                    completedJobs = mechanicJobs.count { it.status == JobStatus.COMPLETED.name },
                    inProgressJobs = mechanicJobs.count { it.status == JobStatus.IN_PROGRESS.name },
                    pendingJobs = mechanicJobs.count { it.status == JobStatus.PENDING.name }
                )
            }.sortedByDescending { it.completedJobs }

            FirebaseResult.Success(performanceList)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun generateInvoice(
        job: JobDto,
        labourCost: Double,
        partsCost: Double
    ): FirebaseResult<InvoiceDto> {
        return try {

            val existing = firestore.collection(INVOICES)
                .whereEqualTo("jobId", job.id)
                .get()
                .await()

            if (!existing.isEmpty) {
                val existingInvoice = existing.toObjects(InvoiceDto::class.java).first()
                return FirebaseResult.Success(existingInvoice)
            }

            val invoice = InvoiceDto(
                id = UUID.randomUUID().toString(),
                jobId = job.id,
                clientId = job.clientId,
                labourCost = labourCost,
                partsCost = partsCost,
                totalCost = labourCost + partsCost,
                isPaid = false,
                createdAt = System.currentTimeMillis()
            )

            firestore.collection(INVOICES).document(invoice.id).set(invoice).await()
            FirebaseResult.Success(invoice)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getInvoiceByJob(jobId: String): FirebaseResult<InvoiceDto?> {
        return try {
            val snapshot = firestore.collection(INVOICES)
                .whereEqualTo("jobId", jobId)
                .get()
                .await()
            val invoice = snapshot.toObjects(InvoiceDto::class.java).firstOrNull()
            FirebaseResult.Success(invoice)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getAllInvoices(): FirebaseResult<List<InvoiceDto>> {
        return try {
            val snapshot = firestore.collection(INVOICES).get().await()
            FirebaseResult.Success(
                snapshot.toObjects(InvoiceDto::class.java)
                    .sortedByDescending { it.createdAt }
            )
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun markAsPaid(invoiceId: String): FirebaseResult<Unit> {
        return try {
            firestore.collection(INVOICES).document(invoiceId)
                .update(
                    mapOf(
                        "isPaid" to true,
                        "paidAt" to System.currentTimeMillis()
                    )
                )
                .await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getUnpaidInvoices(): FirebaseResult<List<InvoiceDto>> {
        return try {
            val snapshot = firestore.collection(INVOICES)
                .whereEqualTo("isPaid", false)
                .get()
                .await()
            FirebaseResult.Success(snapshot.toObjects(InvoiceDto::class.java))
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun getFinancialSummary(): FirebaseResult<FinancialSummaryDto> {
        return try {
            val snapshot = firestore.collection(INVOICES).get().await()
            val invoices = snapshot.toObjects(InvoiceDto::class.java)

            val paid = invoices.filter { it.isPaid }
            val unpaid = invoices.filter { !it.isPaid }

            val summary = FinancialSummaryDto(
                totalRevenue = invoices.sumOf { it.totalCost },
                paidAmount = paid.sumOf { it.totalCost },
                unpaidAmount = unpaid.sumOf { it.totalCost },
                totalInvoices = invoices.size,
                paidInvoices = paid.size,
                unpaidInvoices = unpaid.size
            )

            FirebaseResult.Success(summary)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }
}