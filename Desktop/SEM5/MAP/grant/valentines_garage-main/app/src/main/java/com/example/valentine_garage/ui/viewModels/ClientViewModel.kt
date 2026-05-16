package com.example.valentine_garage.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentine_garage.dto.ClientDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.repositories.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val clientRepository: ClientRepository
) : ViewModel() {

    private val _remoteClients = MutableStateFlow<FirebaseResult<List<ClientDto>>>(FirebaseResult.Success(emptyList()))
    val remoteClients: StateFlow<FirebaseResult<List<ClientDto>>> = _remoteClients.asStateFlow()

    val allClients: StateFlow<List<ClientDto>> = clientRepository.getAllClients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchRemoteClients() {
        viewModelScope.launch {
            _remoteClients.value = clientRepository.fetchAllClientsRemote()
        }
    }

    fun addClient(client: ClientDto) {
        viewModelScope.launch {
            clientRepository.insertClient(client)
        }
    }

    fun deleteClient(client: ClientDto) {
        viewModelScope.launch {
            clientRepository.deleteClient(client)
        }
    }
}
