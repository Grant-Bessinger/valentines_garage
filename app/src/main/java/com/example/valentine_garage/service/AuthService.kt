package com.example.valentine_garage.service


import com.example.valentine_garage.dto.EmployeeDto
import com.example.valentine_garage.dto.UserDto
import com.example.valentine_garage.service.helper.FirebaseResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

class AuthService @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    companion object {
        private const val USERS_COLLECTION = "users"
    }

    val currentUserFlow: Flow<UserDto?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                // Fetch role from Firestore each time auth state changes
                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = doc.toObject(UserDto::class.java)
                        trySend(user)
                    }
                    .addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    suspend fun login(email: String, password: String): FirebaseResult<UserDto> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return FirebaseResult.Failure(Exception("Login failed: no user returned"))

            val userDoc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            val user = userDoc.toObject(UserDto::class.java)
                ?: return FirebaseResult.Failure(Exception("User profile not found. Contact admin."))

            if (!user.active) {
                auth.signOut()
                return FirebaseResult.Failure(Exception("Account is disabled. Contact your manager."))
            }

            FirebaseResult.Success(user)
        } catch (e: Exception) {
            FirebaseResult.Failure(Exception(mapAuthError(e.message)))
        }
    }

    fun logout() {
        auth.signOut()
    }

    suspend fun getCurrentUser(): FirebaseResult<UserDto> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return FirebaseResult.Failure(Exception("Not logged in"))
            val doc = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            val user = doc.toObject(UserDto::class.java)
                ?: return FirebaseResult.Failure(Exception("User profile missing"))
            FirebaseResult.Success(user)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun createUser(
        email: String,
        password: String,
        displayName: String,
        role: String
    ): FirebaseResult<EmployeeDto> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid
                ?: return FirebaseResult.Failure(Exception("Failed to create account"))

            val employee = EmployeeDto(
                uid = uid,
                email = email,
                displayName = displayName,
                role = role,
                active = true
            )

            firestore.collection(USERS_COLLECTION).document(uid).set(employee).await()
            FirebaseResult.Success(employee)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }


    fun isLoggedIn(): Boolean = auth.currentUser != null

    suspend fun getUsersByRole(role: String): FirebaseResult<List<EmployeeDto>> {
        return try {
            val snapshot = firestore.collection(USERS_COLLECTION)
                .whereEqualTo("role", role)
                .get()
                .await()
            val employees = snapshot.toObjects(EmployeeDto::class.java)
            FirebaseResult.Success(employees)
        } catch (e: Exception) {
            FirebaseResult.Failure(e)
        }
    }

    suspend fun sendPasswordResetEmail(email: String): FirebaseResult<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            FirebaseResult.Success(Unit)
        } catch (e: Exception) {
            FirebaseResult.Failure(Exception(mapAuthError(e.message)))
        }
    }

    private fun mapAuthError(message: String?): String = when {
        message == null -> "An unknown error occurred"
        "no user record" in message -> "No account found with that email"
        "password is invalid" in message -> "Incorrect password"
        "badly formatted" in message -> "Invalid email format"
        "blocked all requests" in message -> "Too many attempts. Try again later"
        else -> message
    }

}