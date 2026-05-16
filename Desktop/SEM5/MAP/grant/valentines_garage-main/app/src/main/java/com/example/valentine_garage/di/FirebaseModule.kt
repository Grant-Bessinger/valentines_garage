package com.example.valentine_garage.di

import com.example.valentine_garage.service.AuthService
import com.example.valentine_garage.service.ManagerService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideAuthService(
        auth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): AuthService = AuthService(auth, firestore)

    @Provides
    @Singleton
    fun provideManagerService(
        firestore: FirebaseFirestore
    ): ManagerService = ManagerService(firestore)
}