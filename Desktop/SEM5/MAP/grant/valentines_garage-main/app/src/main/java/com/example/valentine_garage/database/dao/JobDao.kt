package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.JobEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity)

    @Query("SELECT * FROM jobs WHERE id = :id")
    suspend fun getJobById(id: String): JobEntity?

    @Query("SELECT * FROM jobs WHERE vehicleId = :vehicleId")
    fun getJobsByVehicle(vehicleId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs WHERE mechanicId = :mechanicId")
    fun getJobsByMechanic(mechanicId: String): Flow<List<JobEntity>>

    @Query("SELECT * FROM jobs")
    fun getAllJobs(): Flow<List<JobEntity>>

    @Delete
    suspend fun deleteJob(job: JobEntity)
}
