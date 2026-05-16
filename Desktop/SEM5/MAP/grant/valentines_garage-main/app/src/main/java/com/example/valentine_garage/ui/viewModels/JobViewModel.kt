package com.example.valentine_garage.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.dto.MechanicPerformanceDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.repositories.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JobViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    private val _remoteJobs = MutableStateFlow<FirebaseResult<List<JobDto>>>(FirebaseResult.Success(emptyList()))
    val remoteJobs: StateFlow<FirebaseResult<List<JobDto>>> = _remoteJobs.asStateFlow()

    private val _mechanicPerformance = MutableStateFlow<FirebaseResult<List<MechanicPerformanceDto>>>(FirebaseResult.Success(emptyList()))
    val mechanicPerformance: StateFlow<FirebaseResult<List<MechanicPerformanceDto>>> = _mechanicPerformance.asStateFlow()

    val allJobs: StateFlow<List<JobDto>> = jobRepository.getAllJobs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchRemoteJobs() {
        viewModelScope.launch {
            _remoteJobs.value = jobRepository.fetchAllJobsRemote()
        }
    }

    fun fetchCompletedJobsRemote() {
        viewModelScope.launch {
            _remoteJobs.value = jobRepository.fetchCompletedJobsRemote()
        }
    }

    fun fetchMechanicPerformance() {
        viewModelScope.launch {
            _mechanicPerformance.value = jobRepository.getMechanicPerformanceRemote()
        }
    }

    fun addJob(job: JobDto) {
        viewModelScope.launch {
            jobRepository.insertJob(job)
        }
    }

    fun getJobsByMechanic(mechanicId: String): StateFlow<List<JobDto>> {
        return jobRepository.getJobsByMechanic(mechanicId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun getJobsByVehicle(vehicleId: String): StateFlow<List<JobDto>> {
        return jobRepository.getJobsByVehicle(vehicleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun deleteJob(job: JobDto) {
        viewModelScope.launch {
            jobRepository.deleteJob(job)
        }
    }
}
