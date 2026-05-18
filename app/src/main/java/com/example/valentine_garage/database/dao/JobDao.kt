package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.ClientEntity
import com.example.valentine_garage.database.entities.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Upsert
    suspend fun upsertJobs(jobs: List<JobEntity>)

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: String): JobEntity?

    @Query("SELECT * FROM jobs WHERE vehicleId = :vehicleId")
    fun getJobsByVehicle(vehicleId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE mechanicId = :mechanicId")
    fun getJobsByMechanic(mechanicId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE isSynced = 0")
    suspend fun getUnsyncedJobs(): List<JobEntity>

    @Query("UPDATE jobs SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: String)

    @Query("DELETE FROM jobs WHERE isSynced = 1 AND id NOT IN (:ids)")
    suspend fun deleteSyncedJobsNotInList(ids: List<String>)

    @Query("DELETE FROM jobs WHERE isSynced = 1")
    suspend fun deleteAllSyncedJobs()

    @Delete
    suspend fun deleteJob(job: JobEntity)
}
