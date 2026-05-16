package com.example.valentine_garage.ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.valentine_garage.dto.FinancialSummaryDto
import com.example.valentine_garage.dto.InvoiceDto
import com.example.valentine_garage.dto.JobDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.helper.sync.SyncManager
import com.example.valentine_garage.ui.repositories.InvoiceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InvoiceViewModel @Inject constructor(
    private val invoiceRepository: InvoiceRepository,
    private val syncManager: SyncManager
) : ViewModel() {

    private val _remoteInvoices = MutableStateFlow<FirebaseResult<List<InvoiceDto>>>(FirebaseResult.Success(emptyList()))
    val remoteInvoices: StateFlow<FirebaseResult<List<InvoiceDto>>> = _remoteInvoices.asStateFlow()

    private val _financialSummary = MutableStateFlow<FirebaseResult<FinancialSummaryDto?>>(FirebaseResult.Success(null))
    val financialSummary: StateFlow<FirebaseResult<FinancialSummaryDto?>> = _financialSummary.asStateFlow()

    val allInvoices: StateFlow<List<InvoiceDto>> = invoiceRepository.getAllInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unpaidInvoices: StateFlow<List<InvoiceDto>> = invoiceRepository.getUnpaidInvoices()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun fetchRemoteInvoices() {
        viewModelScope.launch {
            invoiceRepository.syncRemoteInvoices()
        }
    }

    fun fetchUnpaidInvoicesRemote() {
        viewModelScope.launch {
            _remoteInvoices.value = invoiceRepository.fetchUnpaidInvoicesRemote()
        }
    }

    fun fetchFinancialSummary() {
        viewModelScope.launch {
            _financialSummary.value = invoiceRepository.getFinancialSummaryRemote()
        }
    }

    fun generateInvoiceRemote(job: JobDto, labour: Double, parts: Double) {
        viewModelScope.launch {
            invoiceRepository.generateInvoiceRemote(job, labour, parts)
        }
    }

    fun markAsPaidRemote(invoiceId: String) {
        viewModelScope.launch {
            invoiceRepository.markAsPaidRemote(invoiceId)
        }
    }

    fun addInvoice(invoice: InvoiceDto) {
        viewModelScope.launch {
            invoiceRepository.insertInvoice(invoice)
            syncManager.scheduleSync()
        }
    }

    fun deleteInvoice(invoice: InvoiceDto) {
        viewModelScope.launch {
            invoiceRepository.deleteInvoice(invoice)
        }
    }
}
