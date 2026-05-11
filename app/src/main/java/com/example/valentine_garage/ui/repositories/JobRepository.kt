package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.JobDao
import com.example.valentine_garage.database.entities.JobEntity
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.service.ManagerService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JobRepository @Inject constructor(
    private val jobDao: JobDao,
    private val managerService: ManagerService
) {

    suspend fun insertJob(jobDto: JobDto) {
        jobDao.insertJob(JobEntity.fromDto(jobDto))
    }

    suspend fun getJobById(id: String): JobDto? {
        return jobDao.getJobById(id)?.toDto()
    }

    fun getJobsByVehicle(vehicleId: String): Flow<List<JobDto>> {
        return jobDao.getJobsByVehicle(vehicleId).map { entities ->
            entities.map { it.toDto() }
        }
    }

    fun getJobsByMechanic(mechanicId: String): Flow<List<JobDto>> {
        return jobDao.getJobsByMechanic(mechanicId).map { entities ->
            entities.map { it.toDto() }
        }
    }

    fun getAllJobs(): Flow<List<JobDto>> {
        return jobDao.getAllJobs().map { entities ->
            entities.map { it.toDto() }
        }
    }

    suspend fun deleteJob(jobDto: JobDto) {
        jobDao.deleteJob(JobEntity.fromDto(jobDto))
    }

    // --- Remote ManagerService Methods ---

    suspend fun fetchAllJobsRemote() = managerService.getAllJobs()

    suspend fun fetchCompletedJobsRemote() = managerService.getCompletedJobs()

    suspend fun getMechanicPerformanceRemote() = managerService.getMechanicPerformance()
}
