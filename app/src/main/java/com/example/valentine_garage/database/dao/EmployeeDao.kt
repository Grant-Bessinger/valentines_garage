package com.example.valentine_garage.database.dao

import androidx.room.*
import com.example.valentine_garage.database.entities.ClientEntity
import com.example.valentine_garage.database.entities.EmployeeEntity
import com.example.valentine_garage.database.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmployeeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employees: EmployeeEntity)

    @Query("SELECT * FROM employees WHERE uid = :uid")
    suspend fun getEmployeeById(uid: String): EmployeeEntity?

    @Query("SELECT * FROM employees")
    fun getEmployee(): Flow<EmployeeEntity?>

    @Query("SELECT * FROM employees")
    fun getAllEmployees(): Flow<List<EmployeeEntity>>

    @Query("SELECT * FROM employees WHERE isSynced = 0")
    suspend fun getUnsyncedEmployees(): List<EmployeeEntity>

    @Query("UPDATE employees SET isSynced = 1 WHERE uid = :id")
    suspend fun markSynced(id: String)

    @Delete
    suspend fun deleteEmployee(user: EmployeeEntity)
}
