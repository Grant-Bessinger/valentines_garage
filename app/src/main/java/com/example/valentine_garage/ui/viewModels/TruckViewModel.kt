package com.example.valentine_garage.ui.viewModels

import androidx.lifecycle.ViewModel
import com.example.valentine_garage.database.entities.TruckEntity
import com.example.valentine_garage.ui.repositories.TruckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TruckViewModel @Inject constructor(private val repo: TruckRepository) : ViewModel() {

    suspend fun insertTruck(truck: TruckEntity) = repo.insertTruck(truck)

    suspend fun insertTrucks(trucks: List<TruckEntity>) = repo.insertTrucks(trucks)

    fun getAllTrucks() = repo.getAllTrucks()

    suspend fun getTruckById(id: Long) = repo.getTruckById(id)

    suspend fun updateTruck(truck: TruckEntity) = repo.updateTruck(truck)

    suspend fun deleteTruck(truck: TruckEntity) = repo.deleteTruck(truck)

    suspend fun deleteAllTrucks() = repo.deleteAllTrucks()

}