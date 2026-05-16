package com.example.valentine_garage.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentine_garage.dto.VehicleDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.helper.sync.SyncManager
import com.example.valentine_garage.ui.repositories.VehicleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VehicleViewModel @Inject constructor(
    private val vehicleRepository: VehicleRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _remoteVehicles = MutableStateFlow<FirebaseResult<List<VehicleDto>>>(FirebaseResult.Success(emptyList()))
    val remoteVehicles: StateFlow<FirebaseResult<List<VehicleDto>>> = _remoteVehicles.asStateFlow()

    val allVehicles: StateFlow<List<VehicleDto>> = vehicleRepository.getAllVehicles()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchRemoteVehicles() {
        viewModelScope.launch {
            vehicleRepository.syncRemoteVehicles()
        }
    }

    fun addVehicle(vehicle: VehicleDto) {
        viewModelScope.launch {
            vehicleRepository.insertVehicle(vehicle)
            syncManager.scheduleSync()
        }
    }

    fun getVehiclesByClient(clientId: String): StateFlow<List<VehicleDto>> {
        return vehicleRepository.getVehiclesByClient(clientId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun deleteVehicle(vehicle: VehicleDto) {
        viewModelScope.launch {
            vehicleRepository.deleteVehicle(vehicle)
        }
    }
}
