package com.example.valentine_garage.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.valentine_garage.ui.repositories.TruckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TruckViewModel @Inject constructor(private val repo: TruckRepository) : ViewModel() {

}