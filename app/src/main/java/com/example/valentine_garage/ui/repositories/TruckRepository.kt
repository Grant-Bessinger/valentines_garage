package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.TruckDao
import com.example.valentine_garage.database.entities.TruckEntity
import javax.inject.Inject

class TruckRepository @Inject constructor(private val truckDao: TruckDao) {

    suspend fun insertTruck(truck: TruckEntity) = truckDao.insertTruck(truck)

    suspend fun insertTrucks(trucks: List<TruckEntity>) = truckDao.insertTrucks(trucks)

    fun getAllTrucks() = truckDao.getAllTrucks()

    suspend fun getTruckById(id: Long) = truckDao.getTruckById(id)

    suspend fun updateTruck(truck: TruckEntity) = truckDao.updateTruck(truck)

    suspend fun deleteTruck(truck: TruckEntity) = truckDao.deleteTruck(truck)

    suspend fun deleteAllTrucks() = truckDao.deleteAllTrucks()
}