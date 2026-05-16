package com.example.valentine_garage.ui.repositories

import com.example.valentine_garage.database.dao.EmployeeDao
import com.example.valentine_garage.database.dao.UserDao
import com.example.valentine_garage.database.entities.EmployeeEntity
import com.example.valentine_garage.database.entities.UserEntity
import com.example.valentine_garage.database.roomDatabase.AppDatabase
import com.example.valentine_garage.dto.EmployeeDto
import com.example.valentine_garage.dto.UserDto
import com.example.valentine_garage.service.AuthService
import com.example.valentine_garage.service.helper.FirebaseResult
import com.example.valentine_garage.ui.helper.AuthState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


@OptIn(ExperimentalCoroutinesApi::class)
@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val userDao: UserDao,
    private val employeeDao: EmployeeDao,
    private val database: AppDatabase
) {

    val authState: Flow<AuthState> = authService.currentUserFlow.map { user ->
        if (user != null) AuthState.Authenticated(user)
        else AuthState.Unauthenticated
    }

    suspend fun login(email: String, password: String): FirebaseResult<UserDto> {
        val result = authService.login(email, password)
        if (result is FirebaseResult.Success) {
            userDao.insertUser(UserEntity.fromDto(result.data))
        }
        return result
    }

    suspend fun logout() {
        authService.logout()
        withContext(Dispatchers.IO) {
            database.clearAllTables()
        }
    }

    suspend fun getCurrentUser(): FirebaseResult<UserDto> = authService.getCurrentUser()

    fun isLoggedIn(): Boolean = authService.isLoggedIn()

    suspend fun createUser(
        email: String,
        password: String,
        displayName: String,
        role: String
    ) = authService.createUser(email, password, displayName, role)


    suspend fun syncMechanicsLocal(): FirebaseResult<List<EmployeeDto>> {
        val result = authService.getUsersByRole("MECHANIC")
        if (result is FirebaseResult.Success) {
            result.data.forEach {
                employeeDao.insertEmployee(EmployeeEntity.fromDto(it))
            }
        }
        return result
    }

    // --- Local User Data Methods (from former UserRepository) ---

    suspend fun insertUserLocal(userDto: UserDto) {
        userDao.insertUser(UserEntity.fromDto(userDto))
    }

     fun getUser(): Flow<UserDto?> {
         return userDao.getUser().map { it?.toDto() }
     }

    suspend fun getUserByIdLocal(uid: String): UserDto? {
        return userDao.getUserById(uid)?.toDto()
    }

    fun getAllUsersLocal(): Flow<List<UserDto>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDto() }
        }
    }

    suspend fun deleteUserLocal(userDto: UserDto) {
        userDao.deleteUser(UserEntity.fromDto(userDto))
    }

    fun getAllEmployeeLocal(): Flow<List<EmployeeDto>> {
        return employeeDao.getAllEmployees().map { entities ->
            entities.map { it.toDto() }
        }
    }
}
