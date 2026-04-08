package com.example.valentine_garage.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.valentine_garage.database.entities.TruckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TruckDao {

    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTruck(truck: TruckEntity)

    // Insert multiple
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrucks(trucks: List<TruckEntity>)

    // Get all trucks
    @Query("SELECT * FROM TruckEntity")
    fun getAllTrucks(): Flow<List<TruckEntity>>

    // Get single truck by ID
    @Query("SELECT * FROM TruckEntity WHERE id = :id")
    suspend fun getTruckById(id: Long): TruckEntity?

    // Update
    @Update
    suspend fun updateTruck(truck: TruckEntity)

    // Delete
    @Delete
    suspend fun deleteTruck(truck: TruckEntity)

    // Delete all
    @Query("DELETE FROM TruckEntity")
    suspend fun deleteAllTrucks()

}